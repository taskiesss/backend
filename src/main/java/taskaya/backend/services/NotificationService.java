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

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
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

//    // Notifications strings
    public static final String NEW_CONTRACT_NOTIFICATION= "You have a new contract offer for Job {0}! Review the details and respond to secure the opportunity.";

    public static final String CONTRACT_STARTED_CLIENT_NOTIFICATION = "The contract for Job {0} has started! You can now track the progress and communicate with the freelancers.";

    public static final String CONTRACT_STARTED_FREELANCER_NOTIFICATION = "The contract for Job {0} has started! You can now track the progress and communicate with the client.";

    public static final String CONTRACT_REJECTED_CLIENT_NOTIFICATION = "Your contract for Job {0} has been rejected by the freelancer. You can review the details and consider other options.";

    //show milestone name and job name
    public static  final String MILESTONE_REVIEW_REQUEST_CLIENT_NOTIFICATION = "The freelancer has submitted a review request for the milestone {0} in Job {1}. Please review and provide feedback.";

    public static final String MILESTONE_APPROVAL_FREELANCER_NOTIFICATION = "The client has approved the milestone {0} in Job {1}. You can now proceed with the next steps.";

    //show the name of the community and the name of the role the freelancer is applying for in the community
    public static final String JOIN_REQUEST_COMMUNITY_ADMIN_NOTIFICATION = "A new request to join the community {0} as a {1}. Please review and approve or reject the request.";


    //notification functions
    public void newContractNotification(String jobTitle , User user,NotificationDest notificationDest , String contractId){
        String content = MessageFormat.format(NEW_CONTRACT_NOTIFICATION, jobTitle);
        createAndSendNotification(content, user, notificationDest, contractId);
    }

    public void contractStartedClientNotification(String jobTitle , User user, String contractId){
        String content = MessageFormat.format(CONTRACT_STARTED_CLIENT_NOTIFICATION, jobTitle);
        createAndSendNotification(content, user, NotificationDest.CONTRACT, contractId);
    }
    public void contractStartedFreelancerNotification(String jobTitle , User user,NotificationDest notificationDest , String contractId){
        String content = MessageFormat.format(CONTRACT_STARTED_FREELANCER_NOTIFICATION, jobTitle);
        createAndSendNotification(content, user, notificationDest, contractId);
    }
    public void contractRejectedClientNotification(String jobTitle , User user, String contractId){
        String content = MessageFormat.format(CONTRACT_REJECTED_CLIENT_NOTIFICATION, jobTitle);
        createAndSendNotification(content, user, NotificationDest.CONTRACT, contractId);
    }

    public void milestoneReviewRequestClientNotification(String milestoneName, String jobTitle , User user, String contractId){
        String content = MessageFormat.format(MILESTONE_REVIEW_REQUEST_CLIENT_NOTIFICATION, milestoneName, jobTitle);
        createAndSendNotification(content, user, NotificationDest.CONTRACT, contractId);
    }
    public void milestoneApprovalFreelancerNotification(String milestoneName, String jobTitle , User user,NotificationDest notificationDest, String contractId){
        String content = MessageFormat.format(MILESTONE_APPROVAL_FREELANCER_NOTIFICATION, milestoneName, jobTitle);
        createAndSendNotification(content, user, notificationDest, contractId);
    }

    public void joinRequestCommunityAdminNotification(String communityName, String roleName , User user, String communityId){
        String content = MessageFormat.format(JOIN_REQUEST_COMMUNITY_ADMIN_NOTIFICATION, communityName, roleName);
        createAndSendNotification(content, user, NotificationDest.COMMUNITY_JOBS_AND_TALENTS, communityId);
    }

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
