package taskaya.backend.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.mappers.NotificationResponseMapper;
import taskaya.backend.DTO.notifications.NotificationResponseDTO;
import taskaya.backend.DTO.notifications.NotificationWebSocketDTO;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.Notification;
import taskaya.backend.entity.User;
import taskaya.backend.entity.enums.NotificationDest;
import taskaya.backend.repository.NotificationRepository;
import taskaya.backend.repository.UserRepository;

import java.util.Date;
import java.util.UUID;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;


    @Transactional
    public void createAndSendNotification(String content, User user, NotificationDest type, String routeId) {
        //Send Real-Time notification, and store it in the database
        Notification notification = Notification.builder()
                .content(content)
                .user(user)
                .isRead(false)
                .type(type)
                .routeId(routeId)
                .date(new Date())
                .build();
        notificationRepository.save(notification);

        //increment the notifications count for user
        user.setNewNotifications(user.getNewNotifications()+1);
        userRepository.save(user);

        NotificationResponseDTO notificationDTO = NotificationResponseMapper.toDTO(notification);

        NotificationWebSocketDTO notificationWebSocketDTO = NotificationWebSocketDTO.builder()
                .notification(notificationDTO)
                .newNotificationsCount(user.getNewNotifications())
                .build();

        messagingTemplate.convertAndSend("/notifications/" + user.getId(), notificationWebSocketDTO);
    }

    @Transactional
    public Page<NotificationResponseDTO> getUserNotifications(int page, int size) {
        Sort sort = Sort.by(Sort.Order.desc("date"));
        Pageable pageable = PageRequest.of(page, size, sort);

        //set the new notifications count to 0, as user opens the notifications
        User user = jwtService.getUserFromToken();
        user.setNewNotifications(0);
        userRepository.save(user);

        Page<Notification> notificationPage = notificationRepository.findByUser(user,pageable);
        return NotificationResponseMapper.toDTOPage(notificationPage);
    }

    @Transactional
    @PreAuthorize("@jwtService.isNotificationOwner(#notificationId)")
    public void markAsRead(String notificationId) {
        notificationRepository.findById(UUID.fromString(notificationId)).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
}
