package taskaya.backend.entity.freelancer;


import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "price_per_hour", nullable = false)
    private Double pricePerHour;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;

    @Column(name = "completed_jobs", nullable = false)
    private Integer completedJobs;

    @Column(nullable = false)
    private Integer rate; // Should be validated to [1-5] in logic

    public enum State {
        BUSY, AVAILABLE
    }

    public void setRate(Integer rate) {
        if(!(rate.doubleValue()>5 || rate.doubleValue()<0))
            this.rate = rate;
    }
}
