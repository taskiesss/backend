package taskaya.backend.DTO.mappers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.commons.responses.MyProposalsPageResponseDTO;
import taskaya.backend.DTO.notifications.NotificationWebSocketDTO;
import taskaya.backend.entity.Notification;
import taskaya.backend.entity.work.Proposal;

import java.util.LinkedList;
import java.util.List;

@Component
public class NotificationWebSocketMapper {
    public static NotificationWebSocketDTO toDTO(Notification notification) {
        return NotificationWebSocketDTO.builder()
                .notificationId(notification.getId().toString())
                .content(notification.getContent())
                .isRead(notification.isRead())
                .type(notification.getType())
                .routeId(notification.getRouteId())
                .newNotificationsCount(notification.getUser().getNewNotifications())
                .build();
    }
    public static List<NotificationWebSocketDTO> toDTOList(List<Notification> notifications) {

        List<NotificationWebSocketDTO> result = new LinkedList<>();
        for (Notification notification :notifications){
            result.add(toDTO(notification));
        }
        return result;
    }

    public static Page<NotificationWebSocketDTO> toDTOPage(Page<Notification> notifications){
        return new PageImpl<>(toDTOList(notifications.getContent()), notifications.getPageable(), notifications.getTotalElements());
    }
}
