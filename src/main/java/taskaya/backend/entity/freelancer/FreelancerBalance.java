package taskaya.backend.entity.freelancer;



import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "freelancer_balances")
public class FreelancerBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Double available=0.0;

    @Column(name = "work_in_progress", nullable = false)
    private Double workInProgress=0.0;

}