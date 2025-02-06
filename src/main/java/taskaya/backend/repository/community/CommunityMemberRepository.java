package taskaya.backend.repository.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.community.CommunityMember;
@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember,Integer> {

}
