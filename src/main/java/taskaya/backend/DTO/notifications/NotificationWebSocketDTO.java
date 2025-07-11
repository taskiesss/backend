package taskaya.backend.DTO.notifications;

import lombok.Builder;
import lombok.Data;
import taskaya.backend.entity.enums.NotificationDest;

@Data
@Builder
public class NotificationWebSocketDTO {
    NotificationResponseDTO notification;
    Integer newNotificationsCount ;
}
