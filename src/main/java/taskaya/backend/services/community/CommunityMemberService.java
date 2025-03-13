package taskaya.backend.services.community;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.repository.community.CommunityMemberRepository;

import java.util.List;


@Service
public class CommunityMemberService {
    @Autowired
    private CommunityMemberRepository communityMemberRepository;


    private CommunityService communityService;

    @Autowired
    public void setCommunityService(@Lazy CommunityService communityService) {
        this.communityService = communityService;
    }

    @Transactional
    public void saveMember(CommunityMember communityMember){
        communityMemberRepository.save(communityMember);
        if (!communityMember.getCommunity().getCommunityMembers().contains(communityMember)) {
            communityMember.getCommunity().getCommunityMembers().add(communityMember);
        }
        communityService.updateIsFull(communityMember.getCommunity());
    }

    public CommunityMember findById(Integer id){
        return communityMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CommunityMember not found with id: " + id));
    }

    public List<CommunityMember> getAssignedMembers(List<CommunityMember> positions){
        return positions.stream().filter(communityMember -> communityMember.getFreelancer()!=null).toList();
    }

    public List<CommunityMember> getNotAssignedPositions(List<CommunityMember> positions){
        return positions.stream().filter(communityMember -> communityMember.getFreelancer()==null).toList();
    }



}
