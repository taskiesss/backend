package taskaya.backend.repository.work;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.work.WorkerEntity;

import java.util.UUID;
@Repository
public interface WorkerEntityRepository extends JpaRepository<WorkerEntity, UUID> {
}
