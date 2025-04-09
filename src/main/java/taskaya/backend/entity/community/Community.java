package taskaya.backend.entity.community;

import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.config.Constants;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.freelancer.FreelancerBusiness;
import taskaya.backend.entity.work.WorkerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    private String title;

    private String country;

    @Column(name = "community_name", nullable = false, length = 100)
    private String communityName;


    @Column(name = "profile_picture")
    @Builder.Default
    private String profilePicture = Constants.COMMUNITY_FIRST_PROFILE_PICTURE;

    @Column(name = "cover_photo")
    @Builder.Default
    private String coverPhoto=Constants.FIRST_COVER_PICTURE;


    @OneToOne( cascade = CascadeType.ALL)
    @JoinColumn(name = "worker_entity_id", nullable = false)
    private WorkerEntity workerEntity;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Freelancer admin;


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "freelancer_business_id", referencedColumnName = "id")
    @Builder.Default
    private FreelancerBusiness freelancerBusiness = new FreelancerBusiness();

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
    @Builder.Default
    private Boolean isFull=true;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CommunityMember> communityMembers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperienceLevel experienceLevel = ExperienceLevel.entry_level;


    @ManyToMany (cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(
            name = "community_skill",
            joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;

    public enum CommunityStatus {
        AVAILABLE,
        BUSY
    }


}