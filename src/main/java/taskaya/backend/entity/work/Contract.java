package taskaya.backend.entity.work;


import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.entity.client.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job; // Associated Job

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_entity_id", nullable = false)
    private WorkerEntity workerEntity; // Associated WorkerEntity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client; // Associated Client

    @Column(name = "description", length = 1000)
    private String description; // Contract description

    @Column(name = "cost_per_hour", nullable = false)
    private Double costPerHour; // Cost per hour

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContractStatus status; // Contract status

    @OneToMany
    @JoinColumn(name = "contract_id") // Foreign key in the Milestone table
    private List<Milestone> milestones = new ArrayList<>();

    public enum ContractStatus {
        ACCEPTED,
        REJECTED,
        PENDING,
        FINISHED,
        APPROVED
    }
}