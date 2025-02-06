package taskaya.backend.repository.work;

import org.springframework.data.jpa.repository.JpaRepository;
import taskaya.backend.entity.work.WorkerEntity;

import java.util.UUID;

public interface WorkerEntityRepository extends JpaRepository<WorkerEntity, UUID> {
}
