package taskaya.backend.DTO.freelancers.requests;


import lombok.Builder;
import lombok.Data;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.enums.SortedBy;

import java.util.List;


@Data
@Builder
public class FreenlancerSearchRequestDTO {
    String search ;
    List<Skill> skills;
    List<ExperienceLevel> experienceLevel ;
    float hourlyRateMin ;
    float hourlyRateMax;
    float rate;
    Integer page ;
    Integer size;
    SortedBy sortBy;
    SortDirection sortDirection;
}
