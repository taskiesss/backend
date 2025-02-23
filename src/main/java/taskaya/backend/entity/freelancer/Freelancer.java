package taskaya.backend.entity.freelancer;


import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.config.Constants;
import taskaya.backend.entity.*;
import taskaya.backend.entity.chat.MsgBox;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.work.WorkerEntity;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "freelancers")
public class Freelancer {

    @Id
    private UUID id; // Same UUID as the User entity

    private String name;

    private String title;

    @OneToOne(cascade = CascadeType.ALL , orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "id", referencedColumnName = "id")
    private User user; // References the User entity

    @Column(name = "price_per_hour", nullable = false)
    private Double pricePerHour=0.0;

    @Column(nullable = false)
    private float rate =0;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperienceLevel experienceLevel = ExperienceLevel.entry_level;


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "worker_entity_id", referencedColumnName = "id")
    private WorkerEntity workerEntity;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "freelancer_business_id", referencedColumnName = "id")
    private FreelancerBusiness freelancerBusiness;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "freelancer_id", referencedColumnName = "id")
    @Builder.Default
    private List<FreelancerPortfolio> portfolios= new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "freelancer_id", referencedColumnName = "id")
    @Builder.Default
    private List<EmployeeHistory> employeeHistories= new ArrayList<>();


    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(
            name = "freelancer_skills",
            joinColumns = @JoinColumn(name = "freelancer_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "freelancer_balance_id", referencedColumnName = "id")
    private FreelancerBalance balance;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "cover_photo")
    @Builder.Default
    private String coverPhoto=Constants.FIRST_COVER_PICTURE;

    @Column(length = Constants.MAX_DESCRIPTION_SIZE)
    private String description;

    @Column(name = "cv")
    private String cv; // Path or link to the CV file

    // Replace the single language column with an element collection
    @ElementCollection
    @CollectionTable(name = "freelancer_languages", joinColumns = @JoinColumn(name = "freelancer_id"))
    @Column(name = "language")
    private Set<String> languages = new HashSet<>();

    @Column(name = "country")
    private String country;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "freelancer_id")
    @Builder.Default
    private List<Education> educations= new ArrayList<>();

    @Column()
    private String linkedIn;

    @OneToMany(mappedBy = "freelancer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MsgBox> msgBoxes;

    @ManyToMany
    @JoinTable(
            name = "freelancer_saved_clients",
            joinColumns = @JoinColumn(name = "freelancer_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )

    private Set<Client> savedClients;


    public void setRate(float rate) {
        if(!(rate>5 || rate<0))
            this.rate = rate;
    }
}