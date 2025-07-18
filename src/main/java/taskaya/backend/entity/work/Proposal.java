package taskaya.backend.entity.work;


import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.config.Constants;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.enums.PaymentMethod;
import taskaya.backend.entity.enums.PaymentMethod;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProposalStatus status = ProposalStatus.PENDING;

    @OneToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "proposal_id")
    private List<Milestone> milestones = new ArrayList<>();


    @Column(name = "payment", nullable = false)
    private PaymentMethod payment;

    @Column(name = "attachment")
    private String attachment;  //attachment URL

    @Column(name = "date")
    @Builder.Default
    private Date date= new Date();

    public enum ProposalStatus {
        PENDING, DECLINED,ACCEPTED, HIRED
    }

}