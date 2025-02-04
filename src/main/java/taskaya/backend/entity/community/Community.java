package taskaya.backend.entity.community;

import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.WorkerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @OneToOne
    @JoinColumn(name = "worker_entity_id", nullable = false)
    private WorkerEntity workerEntity;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Freelancer admin;

    @Column(name = "community_name", nullable = false, length = 100)
    private String communityName;

    @Column(name = "average_hours_per_week", nullable = false)
    private float avrgHoursPerWeek;

    @Column(name = "price_per_hour", nullable = false)
    private double pricePerHour;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommunityStatus status;

    @Column(nullable = false)
    private float rate;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "is_full", nullable = false)
    private boolean isFull;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityMember> communityMembers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperienceLevel experienceLevel = ExperienceLevel.entry_level;


    @ManyToMany
    @JoinTable(
            name = "community_skill",
            joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills = new ArrayList<>();

    public enum CommunityStatus {
        AVAILABLE,
        BUSY
    }
}