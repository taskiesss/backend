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
@Table(name = "proposals")
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Primary key

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "job_id", nullable = false)
//    private Job job; // Associated Job

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_entity_id", nullable = false)
    private WorkerEntity workerEntity; // Associated WorkerEntity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client; // Associated Client

    @Column(name = "description", length = 1000)
    private String description; // Proposal description

    @Column(name = "cost_per_hour", nullable = false)
    private Double costPerHour; // Cost per hour

    @OneToOne
    @JoinColumn(name = "proposal_id")
    private Contract contract;


    @OneToMany
    @JoinColumn(name = "proposal_id")
    private List<Milestone> milestones = new ArrayList<>();
}