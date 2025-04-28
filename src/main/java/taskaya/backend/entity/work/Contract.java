package taskaya.backend.entity.work;


import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.config.Constants;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.enums.PaymentMethod;
import taskaya.backend.entity.enums.PaymentMethod;

import java.time.LocalDate;
import java.util.*;

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

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_entity_id", nullable = false)
    private WorkerEntity workerEntity; // Associated WorkerEntity

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id") // Foreign key in the ContractContributor table
    @Builder.Default
    private List<ContractContributor> contractContributors = new LinkedList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client; // Associated Client

    @Column(name = "description", length = Constants.MAX_DESCRIPTION_SIZE)
    private String description; // Contract description

    @Column(name = "cost_per_hour", nullable = false)
    private Double costPerHour; // Cost per hour

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContractStatus status; // Contract status

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contract_id") // Foreign key in the Milestone table
    private List<Milestone> milestones = new ArrayList<>();

    @Column(name = "due_date")
//    @Builder.Default
    Date dueDate;

    @Column(name = "sent_date")
    @Builder.Default
    Date sentDate= new Date();

    @Column(name = "start_date")
    Date startDate;

    @Column(name = "end_date")
    Date endDate;

    @Column(name = "client_rating_for_freelancer")
    Float clientRatingForFreelancer;

    @Column(name = "freelancer_rating_for_client")
    Float freelancerRatingForClient;

    @Column(name = "payment", nullable = false)
    private PaymentMethod payment;

    public enum ContractStatus {
        //before activation
        PENDING ,
        REJECTED ,

        //after activation
        ACTIVE,
        ENDED
    }

    @PrePersist
    private void getDueDateFromMilestones (){
        if (milestones.isEmpty())
            throw new RuntimeException("needs at least 1 milestone");
        Date result =milestones.getFirst().getDueDate();
        for (Milestone milestone :milestones){
            if (milestone.getDueDate().after(result))
                result=milestone.getDueDate();
        }
        dueDate = result;
    }


    public void setCostPerHour(Double costPerHour) {
        if (costPerHour<=0)
            throw new IllegalArgumentException("cost per hour should be greater than 0");

        this.costPerHour = costPerHour;
    }

    public void setStatus(ContractStatus status){

        this.status = status;
        if(this.status == ContractStatus.ACTIVE) {
            startDate = new Date();
            //set milestone number 1 to be active , i am not sure that the first milestone is the first one in the list
            Milestone milestone = milestones.stream().filter(milestone1 -> milestone1.getNumber()==1)
                    .findFirst()
                    .orElseThrow(()->new RuntimeException("milestone 1 not found"));
            milestone.setStatus(Milestone.MilestoneStatus.IN_PROGRESS);
        }
    }
}