package taskaya.backend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    @Autowired
    Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", folderName));
        return uploadResult.get("secure_url").toString();
    }


    public boolean deleteFile(String fileUrl) throws IOException {
        // Extract public ID from URL
        String publicId = extractPublicId(fileUrl);

        if (publicId == null) {
            throw new IllegalArgumentException("Invalid file URL");
        }

        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        return "ok".equals(result.get("result"));
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

            // Remove file extension (.png, .jpg, etc.)
            int lastDotIndex = relativePath.lastIndexOf('.');
            if (lastDotIndex != -1) {
                relativePath = relativePath.substring(0, lastDotIndex);
            }

            return relativePath;
        } catch (Exception e) {
            return null;
        }
    }


}
