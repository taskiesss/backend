package taskaya.backend.entity.client;


import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.User;
import taskaya.backend.entity.chat.MsgBox;
import taskaya.backend.entity.freelancer.Freelancer;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "clients")
public class Client {

    @Id
    private UUID id; // Same UUID as the User entity

    @OneToOne(cascade = CascadeType.ALL , orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "id", referencedColumnName = "id")
    private User user; // References the User entity

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(length = 500)
    private String description;

    @Column(name = "language")
    private String language;

    @Column(name = "country")
    private String country;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "client_business_id", referencedColumnName = "id")
    private ClientBusiness clientBusiness;

    @ManyToMany
    @JoinTable(
            name = "client_skills",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "client_balance_id", referencedColumnName = "id")
    private ClientBalance balance;

    @ManyToMany
    @JoinTable(
            name = "saved_freelancers",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "freelancer_id")
    )
    private Set<Freelancer> savedFreelancers;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MsgBox> msgBoxes; // Messages linked to this client
}