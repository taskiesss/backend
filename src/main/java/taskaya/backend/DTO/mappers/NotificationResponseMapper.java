package taskaya.backend.DTO.mappers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.notifications.NotificationResponseDTO;
import taskaya.backend.entity.Notification;

import java.util.LinkedList;
import java.util.List;

@Component
public class NotificationResponseMapper {
    public static NotificationResponseDTO toDTO(Notification notification) {
        return NotificationResponseDTO.builder()
                .notificationId(notification.getId().toString())
                .content(notification.getContent())
                .isRead(notification.isRead())
                .type(notification.getType())
                .routeId(notification.getRouteId())
                .build();
    }
    public static List<NotificationResponseDTO> toDTOList(List<Notification> notifications) {

        List<NotificationResponseDTO> result = new LinkedList<>();
        for (Notification notification :notifications){
            result.add(toDTO(notification));
        }
        return result;
    }

    public static Page<NotificationResponseDTO> toDTOPage(Page<Notification> notifications){
        return new PageImpl<>(toDTOList(notifications.getContent()), notifications.getPageable(), notifications.getTotalElements());
    }
}
