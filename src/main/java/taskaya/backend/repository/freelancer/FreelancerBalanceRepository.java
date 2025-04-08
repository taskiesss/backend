package taskaya.backend.repository.freelancer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.freelancer.FreelancerBalance;


@Repository
public interface FreelancerBalanceRepository extends JpaRepository<FreelancerBalance, Integer> {
}
