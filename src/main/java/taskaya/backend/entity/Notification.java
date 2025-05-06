package taskaya.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.entity.enums.NotificationDest;

import java.util.Date;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Primary key

    @Column(name = "title", nullable = false, length = 255)
    String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    boolean isRead= false;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    NotificationDest type;

    @Column(name = "route_id", nullable = false)
    String routeId;

    @Column(name = "date", nullable = false)
    @Builder.Default
    private Date date = new Date();
}
