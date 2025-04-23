package taskaya.backend.DTO.communities.requests;

import lombok.Builder;
import lombok.Data;
import taskaya.backend.entity.Skill;

import java.util.List;

@Data
@Builder
public class CommunityCreateRequestDTO {
    private String title;
    private String communityName;
    private String description;
    private Float pricePerHour;
    private Integer avrgHoursPerWeek;
    private List<Skill> skills;
    private MemberForCreateCommunityDTO adminRole;
    private List<MemberForCreateCommunityDTO> communityPositions;
}
