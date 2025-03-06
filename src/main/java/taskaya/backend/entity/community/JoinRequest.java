package taskaya.backend.entity.community;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.entity.freelancer.Freelancer;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Integer for optimized performance
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "freelancer_id", nullable = false)
    private Freelancer freelancer;

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    @JsonIgnore // Avoid exposing this field in the JSON response
    private CommunityMember position;

    @ManyToOne
    @JoinColumn(name = "community_id", nullable = false)
    @JsonIgnore // Avoid exposing this field in the JSON response
    private Community community;
}