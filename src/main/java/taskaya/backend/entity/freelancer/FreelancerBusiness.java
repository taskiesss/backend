package taskaya.backend.entity.freelancer;


import jakarta.persistence.*;
import lombok.*;
import taskaya.backend.entity.User;
import taskaya.backend.entity.enums.ExperienceLevel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "freelancer_businesses")
public class FreelancerBusiness {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false )
    private State state = State.AVAILABLE;

    @Column(name = "completed_jobs", nullable = false)
    private Integer completedJobs=0;


    public enum State {
        BUSY, AVAILABLE
    }


}
