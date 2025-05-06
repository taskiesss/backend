package taskaya.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskaya.backend.DTO.SimpleResponseDTO;
import taskaya.backend.DTO.notifications.NotificationWebSocketDTO;
import taskaya.backend.services.NotificationService;

@RestController
@RequestMapping
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    @GetMapping("/api/notifications/{userId}")
    public ResponseEntity<Page<NotificationWebSocketDTO>> getUserNotifications(@PathVariable String userId,
                                                                               @RequestParam int page,
                                                                               @RequestParam int size) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId, page, size));
    }


    @PostMapping("/api/notifications/mark-as-read/{notificationId}")
    public ResponseEntity<?> markAsRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.status(HttpStatus.OK).body(SimpleResponseDTO.builder().message("Notification Marked Read!").build());
    }
}
