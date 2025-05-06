package taskaya.backend.DTO.notifications;

import lombok.Builder;
import lombok.Data;
import taskaya.backend.entity.enums.NotificationDest;

@Data
@Builder
public class NotificationWebSocketDTO {
    String notificationId;
    String content;
    boolean isRead;
    NotificationDest type;
    String routeId;
    Integer newNotificationsCount ;
}
