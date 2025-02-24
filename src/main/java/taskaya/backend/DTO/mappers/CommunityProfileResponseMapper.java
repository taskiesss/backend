package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.communities.responses.CommunityProfileResponseDTO;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.community.Community;

@Component
public class CommunityProfileResponseMapper {

    public static CommunityProfileResponseDTO toDTO(Community community){
        return CommunityProfileResponseDTO.builder()
                .title(community.getTitle())
                .profilePicture(community.getProfilePicture())
                .name(community.getCommunityName())
                .country(community.getCountry())
                .coverPhoto(community.getCoverPhoto())
                .pricePerHour(community.getPricePerHour())
                .rate(community.getRate())
                .description(community.getDescription())
                .communityMembers(CommunityMemberCommunityProfileResponseMapper.toDTOList(community.getCommunityMembers()))
                .skills(community.getSkills().stream().map(Skill::getName).toList())
                .isFull(community.getIsFull())
                .build();
    }
}
