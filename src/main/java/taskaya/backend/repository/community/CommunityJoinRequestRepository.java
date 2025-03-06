package taskaya.backend.repository.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.JoinRequest;
import taskaya.backend.entity.work.Job;


import java.util.List;
import java.util.UUID;

@Repository
public interface CommunityJoinRequestRepository extends JpaRepository<JoinRequest, UUID>{
    List<JoinRequest> findAllByCommunity(Community community);
}
