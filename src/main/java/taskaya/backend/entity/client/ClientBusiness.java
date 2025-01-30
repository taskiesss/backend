package taskaya.backend.entity.client;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "client_businesses")
public class ClientBusiness {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "completed_jobs", nullable = false)
    private Integer completedJobs=0;

    @Column(nullable = false)
    private float rate = 0F; // Should be validated to [1-5] in logic

    @Column(name = "total_spent", nullable = false)
    private Double totalSpent=0.0;
}