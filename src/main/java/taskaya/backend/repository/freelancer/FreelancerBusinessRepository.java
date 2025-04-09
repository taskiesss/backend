package taskaya.backend.repository.freelancer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.freelancer.FreelancerBusiness;

@Repository
public interface FreelancerBusinessRepository extends JpaRepository<FreelancerBusiness, Integer> {
}
