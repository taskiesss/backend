package taskaya.backend.repository.community;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.entity.community.JoinRequest;
import taskaya.backend.entity.freelancer.Freelancer;


import java.util.UUID;

@Repository
public interface CommunityJoinRequestRepository extends JpaRepository<JoinRequest, Integer>{
    Page<JoinRequest> findAllByCommunity(Community community, Pageable pageable);

    JoinRequest findByFreelancer(Freelancer freelancer);

    void deleteByPosition(CommunityMember communityMember);
}
