package taskaya.backend.services.community;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.repository.community.CommunityMemberRepository;


@Service
public class CommunityMemberService {
    @Autowired
    private CommunityMemberRepository communityMemberRepository;

    @Transactional
    public void addMember(CommunityMember communityMember){
        communityMemberRepository.save(communityMember);
    }

    public CommunityMember findById(Integer id){
        return communityMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CommunityMember not found with id: " + id));
    }

}
