package taskaya.backend.entity.freelancer;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "freelancer_portfolios")
public class FreelancerPortfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "portfolio_pdf", nullable = false)
    private String portfolioPdf; // Path or link to the portfolio PDF

    @Column(name = "portfolio_pdf", nullable = false)
    private String name; // Path or link to the portfolio PDF


}