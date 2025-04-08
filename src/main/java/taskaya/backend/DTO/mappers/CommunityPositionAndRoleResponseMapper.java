package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.communities.communityMember.responses.CommunityMemberSettingsResponseDTO;
import taskaya.backend.entity.community.CommunityMember;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommunityPositionAndRoleResponseMapper {
    public static CommunityMemberSettingsResponseDTO toDTO(CommunityMember communityMember,Integer sum){
        Integer currentPercentage = 0;
        if (communityMember.getFreelancer() != null) {
            currentPercentage = (int)((communityMember.getPositionPercent() / (float)sum)*100);
        }

        CommunityMemberSettingsResponseDTO memberDTO = CommunityMemberSettingsResponseDTO.builder()
                .positionId(communityMember.getId())
                .description(communityMember.getDescription())
                .currentPercentage(currentPercentage)
                .futurePercentage((int)communityMember.getPositionPercent())
                .build();
        if(communityMember.getFreelancer() != null)
            memberDTO.setNameAndPicture(CommunityMemberCommunityProfileResponseMapper.toDTO(communityMember));
        return memberDTO;
    }

    public static List<CommunityMemberSettingsResponseDTO> toDTOList(List<CommunityMember> communityMembers) {
        Integer sum = (int) communityMembers.stream()
                .filter(cm -> cm.getFreelancer() != null)
                .mapToDouble(CommunityMember::getPositionPercent)
                .sum();

        return communityMembers.stream()
                .map(cm -> toDTO(cm, sum))
                .collect(Collectors.toList());
    }

}
