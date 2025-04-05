package taskaya.backend.entity.community;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.entity.freelancer.Freelancer;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Integer for optimized performance
    private Long id;

    @ManyToOne
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;
    //unique within a community
    @Column(name = "position_name", nullable = false, length = 100)
    private String positionName;

    @Column(name = "description",length = 256)
    private String description;

    @Column(name = "position_percent", nullable = false)
    private float positionPercent;

    @ManyToOne
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Avoid circular references in JSON serialization
    private List<JoinRequest> joinRequests = new ArrayList<>();

    @OneToMany(mappedBy = "communityMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();


    //unique within a community
    //make sure that you set the community before the position name for the Community member
    public void setPositionName(String positionName) {
        for (CommunityMember communityMember : community.getCommunityMembers()) {
            if (communityMember.getPositionName().equals(positionName)) {
                throw new IllegalArgumentException("Position name must be unique within a community");
            }
        }
    }
}