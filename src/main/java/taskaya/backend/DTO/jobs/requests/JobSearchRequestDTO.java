package taskaya.backend.DTO.jobs.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.enums.ProjectLength;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.enums.SortedBy;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//mapper name :
public class JobSearchRequestDTO {
    private String search;
    private List<Skill> skills;
    private List<ExperienceLevel> experienceLevel;
    private double hourlyRateMin;
    private double hourlyRateMax;
    private List<ProjectLength> projectLength;
    private int page;
    private int size;
    private SortedBy sortBy;
    private SortDirection sortDirection;
    private float rate ;
}
