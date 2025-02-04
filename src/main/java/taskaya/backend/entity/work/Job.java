package taskaya.backend.entity.work;

import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.enums.ExperienceLevel;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    @Column(name = "started_at")
    private Date startedAt;

    @Column(name = "ended_at")
    private Date endedAt;

    @Column(name = "starting_cost_per_hour", nullable = false)
    private double startingCostPerHour;

    @Column(name = "ending_cost_per_hour")
    private double endingCostPerHour;

    @OneToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false )
    private ExperienceLevel experienceLevel=ExperienceLevel.entry_level;

    @ManyToMany
    @JoinTable(
            name = "job_skill",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills = new ArrayList<>();

    public enum JobStatus {
        IN_PROGRESS,
        NOT_ASSIGNED,
        DONE
    }
}