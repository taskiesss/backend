package taskaya.backend.entity.work;

import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.entity.freelancer.Freelancer;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "contract_contributers")
public class ContractContributor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne( optional = false)
    @JoinColumn(name = "freelancer_id", nullable = false)
    private Freelancer freelancer;

    @Column(nullable = false)
    private float Percentage ;
}
