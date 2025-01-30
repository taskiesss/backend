package taskaya.backend.repository.freelancer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.freelancer.Freelancer;

import java.util.UUID;

@Repository
public interface FreelancerRepository extends JpaRepository<Freelancer, UUID> {
}
