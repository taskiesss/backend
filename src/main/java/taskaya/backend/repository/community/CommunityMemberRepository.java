package taskaya.backend.repository.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.entity.freelancer.Freelancer;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember,Integer> {

    @Query("""
        SELECT CASE WHEN COUNT(cm) > 0 THEN true ELSE false END 
        FROM CommunityMember cm 
        WHERE cm.community.uuid = :communityId AND cm.freelancer.id = :freelancerId
    """)
    boolean isMember(UUID communityId, UUID freelancerId);

    Optional<CommunityMember> findByCommunityAndFreelancer(Community community , Freelancer freelancer);
    Optional<CommunityMember> findByCommunityAndPositionName(Community community , String positionName);
    Optional<CommunityMember> findByIdAndCommunityUuid(Long memberId, UUID communityId);
}
