package taskaya.backend.entity.freelancer;


import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.entity.*;
import taskaya.backend.entity.chat.MsgBox;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.work.WorkerEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    @OneToOne(cascade = CascadeType.ALL , orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "id", referencedColumnName = "id")
    private User user; // References the User entity

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "worker_entity_id", referencedColumnName = "id")
    private WorkerEntity workerEntity;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "freelancer_business_id", referencedColumnName = "id")
    private FreelancerBusiness freelancerBusiness;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "freelancer_id", referencedColumnName = "id")
    private List<FreelancerPortfolio> portfolios;

    @ManyToMany
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

    private String title;

    @Column(length = 500) // Assuming a longer description
    private String description;

    @Column(name = "cv")
    private String cv; // Path or link to the CV file

    @Column(name = "language")
    private String language;

    @Column(name = "country")
    private String country;

    @Column(name = "education", length = 500)
    private String education;



    @OneToMany(mappedBy = "freelancer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MsgBox> msgBoxes;
    @ManyToMany
    @JoinTable(
            name = "freelancer_saved_clients",
            joinColumns = @JoinColumn(name = "freelancer_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )

    private Set<Client> savedClients;
}