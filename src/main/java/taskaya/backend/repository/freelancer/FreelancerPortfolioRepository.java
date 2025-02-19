package taskaya.backend.repository.freelancer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.freelancer.FreelancerPortfolio;

import java.util.UUID;

@Repository
public interface FreelancerPortfolioRepository extends JpaRepository<FreelancerPortfolio, Integer> {
}
