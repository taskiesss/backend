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
        String uniqueFilename = UUID.randomUUID().toString();

        // Determine the correct resource type
        String resourceType = isImage(fileExtension) ? "image" : "raw";

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

    private boolean isImage(String extension) {
        return extension.matches("\\.(png|jpg|jpeg|gif|bmp|webp|tiff|svg)$");
    }

    public boolean deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new IllegalArgumentException("File URL cannot be null or empty");
        }

        // Extract the public ID from the URL
        String publicId = extractPublicId(fileUrl);

        // Determine the resource type (image or raw)
        String resourceType;
        if(isImageExtension(publicId)){
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

    private boolean isImageExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return false; // No extension or ends with a dot
        }

        String extension = filename.substring(lastDotIndex + 1).toLowerCase(); // Extract extension
        return extension.matches("png|jpg|jpeg|gif|bmp|webp|tiff|svg"); // Match extension
    }


    private String extractPublicId(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null; // Invalid input
        }

        // Find the last "/" to get the filename
        int lastSlashIndex = fileUrl.lastIndexOf('/');
        if (lastSlashIndex == -1 || lastSlashIndex == fileUrl.length() - 1) {
            return null; // No filename found
        }

        return fileUrl.substring(lastSlashIndex + 1);
    }

}
