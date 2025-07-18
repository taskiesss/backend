package taskaya.backend.DTO.communities.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.enums.SortedBy;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunitySearchRequestDTO {
    private String search;
    private List<Skill> skills;
    private List<ExperienceLevel> experienceLevel;
    private float hourlyRateMin;
    private float hourlyRateMax;
    private Boolean isFull;
    private float rate;
    private Integer page;
    private Integer size;
    private SortedBy sortBy;
    private SortDirection sortDirection;

}
