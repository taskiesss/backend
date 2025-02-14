package taskaya.backend.repository.work;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.work.Milestone;

import java.util.UUID;
@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, UUID> {
}
