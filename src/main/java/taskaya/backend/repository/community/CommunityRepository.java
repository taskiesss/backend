package taskaya.backend.repository.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.WorkerEntity;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommunityRepository extends JpaRepository<Community, UUID>, JpaSpecificationExecutor<Community> {
    Optional<Community> findByCommunityName(String communityName);
    List<Community> findAllByAdmin(Freelancer admin);
    Optional<Community> findByWorkerEntity(WorkerEntity workerEntity);

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END 
        FROM Community c 
        WHERE c.uuid = :communityId AND c.admin.id = :userId
    """)
    boolean isAdmin(UUID communityId, UUID userId);

}
