package taskaya.backend.entity.work;


import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.config.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "milestones")
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Primary key

    @Column(name = "name", nullable = false, length = 255)
    private String name; // Milestone name

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate; // Start date

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private Date dueDate; // Due date

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate; // End date

    @Column(name = "number", nullable = false)
    private Integer number; // Milestone number

    @Column(name = "description", length = Constants.MAX_DESCRIPTION_SIZE)
    private String description; // Description

    @Column(name = "estimated_hours")
    private Integer estimatedHours; // Estimated hours

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MilestoneStatus status; // Status

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "milestone_id")
    private List<DeliverableFile> deliverableFiles=new ArrayList<>();;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "milestone_id")
    private List<DeliverableLink> deliverableLinks = new ArrayList<>();

    public enum MilestoneStatus {
        IN_PROGRESS,
        NOT_STARTED,
        PENDING_REVIEW,
        APPROVED
    }
}