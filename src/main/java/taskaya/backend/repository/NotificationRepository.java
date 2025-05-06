package taskaya.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.Notification;
import taskaya.backend.entity.User;
import java.util.List;
import java.util.UUID;
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUser(User user, Pageable pageable);
}
