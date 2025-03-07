package taskaya.backend.repository.community;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.JoinRequest;


import java.util.UUID;

@Repository
public interface CommunityJoinRequestRepository extends JpaRepository<JoinRequest, UUID>{
    Page<JoinRequest> findAllByCommunity(Community community, Pageable pageable);
}
