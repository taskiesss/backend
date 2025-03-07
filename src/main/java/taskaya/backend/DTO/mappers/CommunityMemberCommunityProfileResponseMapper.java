package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.communities.communityMember.responses.CommunityMemberCommunityProfileDTO;
import taskaya.backend.DTO.communities.responses.CommunitySearchResponseDTO;
import taskaya.backend.config.Constants;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.CommunityMember;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommunityMemberCommunityProfileResponseMapper {

    public static CommunityMemberCommunityProfileDTO toDTO(CommunityMember communityMember){
        return CommunityMemberCommunityProfileDTO.builder()
                .name(communityMember.getFreelancer().getName())
                .position(communityMember.getPositionName())
                .freelancerId(communityMember.getFreelancer().getId().toString())
                .freelancerProfilePicture( communityMember.getFreelancer().getProfilePicture())
                .isAdmin(communityMember.getCommunity().getAdmin().getId().equals(communityMember.getFreelancer().getId()))
                .build();
    }

    public static List<CommunityMemberCommunityProfileDTO> toDTOList(List<CommunityMember> members){
        return members.stream()
                .filter(member -> member.getFreelancer() != null)
                .map(CommunityMemberCommunityProfileResponseMapper::toDTO)
                .collect(Collectors.toList());
    }
}
