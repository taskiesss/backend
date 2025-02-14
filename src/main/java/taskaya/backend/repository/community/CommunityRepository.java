package taskaya.backend.repository.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.work.WorkerEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommunityRepository extends JpaRepository<Community, UUID>, JpaSpecificationExecutor<Community> {
    Optional<Community> findByCommunityName(String communityName);
    Optional<Community> findByWorkerEntity(WorkerEntity workerEntity);

}
