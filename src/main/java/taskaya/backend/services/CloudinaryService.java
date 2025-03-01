package taskaya.backend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {
    @Autowired
    Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        // Extract original filename and extension
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        // Generate a unique filename **without adding the extension twice**
        String uniqueFilename = UUID.randomUUID().toString() + originalFilename.substring(0, originalFilename.lastIndexOf("."));

        // Determine the correct resource type
        String resourceType = isImageOrPdf(fileExtension) ? "image" : "raw";

        // Upload file while ensuring extension is preserved only once
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folderName,
                        "resource_type", resourceType,
                        "public_id", uniqueFilename, // Unique name without extension
                        "format", fileExtension.replace(".", "") // Ensures the correct extension
                ));
        return uploadResult.get("secure_url").toString();
    }

    private boolean isImageOrPdf(String extension) {
        return extension.matches("\\.(png|jpg|jpeg|gif|bmp|webp|tiff|svg|pdf)$");
    }

    public boolean deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new IllegalArgumentException("File URL cannot be null or empty");
        }

        // Extract the public ID from the URL
        String publicId = extractPublicId(fileUrl);

        // Determine the resource type (image or raw)
        String resourceType;
        if(isImageOrPdfExtension(publicId)){
            resourceType = "image";
            int dotIndex = publicId.indexOf('.');
            publicId = publicId.substring(0, dotIndex);
        }else{
            resourceType = "raw";
        }

        // Delete the file from Cloudinary
        Map<String, Object> result = cloudinary.uploader().destroy(publicId,
                ObjectUtils.asMap("resource_type", resourceType));


        // Check if the deletion was successful
        return "ok".equals(result.get("result"));
    }

    private boolean isImageOrPdfExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return false; // No extension or ends with a dot
        }

        String extension = filename.substring(lastDotIndex + 1).toLowerCase(); // Extract extension
        return extension.matches("png|jpg|jpeg|gif|bmp|webp|tiff|svg|pdf"); // Match extension
    }


    private String extractPublicId(String fileUrl) {
        try {
            String toFind = "/upload/";
            int index = fileUrl.indexOf(toFind);
            if (index == -1) {
                return null;
            }

            // Extract the part after "/upload/"
            String relativePath = fileUrl.substring(index + toFind.length());

            // Remove the versioning (v1234567890/)
            relativePath = relativePath.replaceFirst("^v\\d+/", "");

            return relativePath;
        } catch (Exception e) {
            return null;
        }
    }

}
