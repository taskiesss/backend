package taskaya.backend.entity.work;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.enums.ProjectLength;

import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Column(nullable = false, length = 40)
    private String title;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private WorkerEntity assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "posted_at", updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date postedAt ;

    @Column(name = "started_at")
    private Date startedAt;

    @Column(name = "ended_at")
    private Date endedAt;

    @Column(name = "expected_cost_per_hour")
    private double expectedCostPerHour;

    @OneToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @Enumerated(EnumType.STRING)
    @Column
    private ExperienceLevel experienceLevel;

    @Enumerated(EnumType.STRING)
    @Column
    private ProjectLength projectLength;


    @ManyToMany
    @JoinTable(
            name = "job_skill",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;

    public enum JobStatus {
        IN_PROGRESS,
        NOT_ASSIGNED,
        DONE
    }

    @PrePersist
    protected void onCreate() {
        this.postedAt = new Date();
    }

}