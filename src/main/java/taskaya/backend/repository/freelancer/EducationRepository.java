package taskaya.backend.repository.freelancer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.freelancer.Education;


@Repository
public interface EducationRepository extends JpaRepository<Education, Integer> {
}
