package taskaya.backend.entity.chat;



import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.freelancer.Freelancer;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "msg_boxes")
public class MsgBox {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "freelancer_id", nullable = false)
    private Freelancer freelancer;

    @Column(name = "last_sent", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastSent;
}