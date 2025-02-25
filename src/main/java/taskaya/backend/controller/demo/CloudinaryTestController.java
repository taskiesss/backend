package taskaya.backend.controller.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import taskaya.backend.services.CloudinaryService;

@RestController
public class CloudinaryTestController {

    @Autowired
    CloudinaryService fileUploadService;

    @PostMapping("/cloudinary/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileUploadService.uploadFile(file,"cover_photos");
            return ResponseEntity.ok("File upload status: " + url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/cloudinary/delete")
    public ResponseEntity<String> deleteFile(@RequestParam String file) {
        try {
            boolean url = fileUploadService.deleteFile(file);
            return ResponseEntity.ok("File delete status: " + url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }
}