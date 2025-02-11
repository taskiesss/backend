package taskaya.backend.DTO.mappers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.search.communities.CommunitySearchResponseDTO;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.community.Community;

import java.util.LinkedList;
import java.util.List;

@Component
public class CommunitySearchResponseMapper {

    public static CommunitySearchResponseDTO toDTO(Community community){
        return CommunitySearchResponseDTO.builder()
                .id(community.getUuid())
                .name(community.getCommunityName())
                .description((community.getDescription()!= null &&community.getDescription().length()>256)? community.getDescription().substring(0,256)+"..." : community.getDescription())
                .skills(community.getSkills().stream().map(Skill::getName).toList())
                .memberCount(community.getCommunityMembers().size())
                .rate(community.getRate())
                .experienceLevel(community.getExperienceLevel())
                .pricePerHour(community.getPricePerHour())
                .profilePicture(community.getProfilePicture())
                .isFull(community.getIsFull())
                .build();
    }

    public static List<CommunitySearchResponseDTO> toDTOList(List<Community> communities) {
        List<CommunitySearchResponseDTO> result = new LinkedList<>();
        for (Community community : communities) {
            result.add(toDTO(community));
        }
        return result;
    }

    public static Page<CommunitySearchResponseDTO> toDTOPage(Page<Community> communities) {
        return new PageImpl<>(toDTOList(communities.getContent()), communities.getPageable(), communities.getTotalElements());
    }
}
