package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.communities.communityMember.responses.CommunityMemberCommunityProfileDTO;
import taskaya.backend.DTO.communities.responses.CommunitySearchResponseDTO;
import taskaya.backend.config.Constants;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.CommunityMember;

import java.util.LinkedList;
import java.util.List;

@Component
public class CommunityMemberCommunityProfileResponseMapper {

    public static CommunityMemberCommunityProfileDTO toDTO(CommunityMember communityMember){
        return CommunityMemberCommunityProfileDTO.builder()
                .name(communityMember.getFreelancer()==null?"open position":communityMember.getFreelancer().getName())
                .position(communityMember.getPositionName())
                .freelancerId(communityMember.getFreelancer()==null?"not assigned":communityMember.getFreelancer().getId().toString())
                .freelancerProfilePicture(communityMember.getFreelancer()==null? Constants.FIRST_PROFILE_PICTURE :communityMember.getFreelancer().getProfilePicture())
                .build();
    }

    public static List<CommunityMemberCommunityProfileDTO> toDTOList(List<CommunityMember> members){
        List<CommunityMemberCommunityProfileDTO> result = new LinkedList<>();
        for (CommunityMember member : members) {
            result.add(toDTO(member));
        }
        return result;
    }
}
