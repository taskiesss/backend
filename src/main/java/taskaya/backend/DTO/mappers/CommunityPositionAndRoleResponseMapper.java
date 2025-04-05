package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.communities.communityMember.responses.CommunityMemberSettingsResponseDTO;
import taskaya.backend.entity.community.CommunityMember;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommunityPositionAndRoleResponseMapper {
    public static CommunityMemberSettingsResponseDTO toDTO(CommunityMember communityMember){
        return CommunityMemberSettingsResponseDTO.builder()
                .nameAndPicture(CommunityMemberCommunityProfileResponseMapper.toDTO(communityMember))
                .positionId(communityMember.getId())
                .description(communityMember.getDescription())
                .positionPercent((int)communityMember.getPositionPercent())
                .build();
    }

    public static List<CommunityMemberSettingsResponseDTO> toDTOList(List<CommunityMember> communityMembers) {
        return communityMembers.stream()
                .filter(member -> member.getFreelancer() != null) // Add filter as per your condition
                .map(CommunityPositionAndRoleResponseMapper::toDTO)
                .collect(Collectors.toList());
    }

}
