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


    @Column(name = "completed_jobs", nullable = false)
    private Integer completedJobs=0;

    private Double avgHoursPerWeek;




}
