package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.communities.communityMember.responses.CommunityMemberCommunityProfileDTO;
import taskaya.backend.DTO.communities.communityMember.responses.CommunityMemberSettingsResponseDTO;
import taskaya.backend.config.Constants;
import taskaya.backend.entity.community.CommunityMember;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommunityPositionAndRoleResponseMapper {
    public static CommunityMemberSettingsResponseDTO toDTO(CommunityMember communityMember,Integer sum){
        Double currentPercentage = 0.0;
        if (communityMember.getFreelancer() != null) {
            currentPercentage = Double.valueOf(String.format("%.2f",
                    ((communityMember.getPositionPercent() / (double) sum) * 100)));
        }

        CommunityMemberSettingsResponseDTO memberDTO = CommunityMemberSettingsResponseDTO.builder()
                .positionId(communityMember.getId())
                .description(communityMember.getDescription())
                .currentPercentage(currentPercentage)
                .futurePercentage((int)communityMember.getPositionPercent())
                .build();
        if(communityMember.getFreelancer() != null)
            memberDTO.setNameAndPicture(CommunityMemberCommunityProfileResponseMapper.toDTO(communityMember));
        else {
            memberDTO.setNameAndPicture(CommunityMemberCommunityProfileDTO.builder()
                            .name("Unassinged")
                            .freelancerProfilePicture(Constants.FIRST_PROFILE_PICTURE)
                            .position(communityMember.getPositionName())
                    .build());
        }

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
