package taskaya.backend.entity.work;


import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.config.Constants;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.enums.Payment;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job; // Associated Job

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_entity_id", nullable = false)
    private WorkerEntity workerEntity; // Associated WorkerEntity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client; // Associated Client

    @Column(name = "cover_letter", length = Constants.MAX_LETTER_LENGTH)
    private String coverLetter; // Proposal cover letter

    @Column(name = "cost_per_hour", nullable = false)
    private Double costPerHour; // Cost per hour

    @OneToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;


    @OneToMany
    @JoinColumn(name = "proposal_id")
    private List<Milestone> milestones = new ArrayList<>();


    @Column(name = "payment", nullable = false)
    private Payment payment;

    @Column(name = "attachment")
    private String attachment;  //attachment URL
}