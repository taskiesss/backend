package taskaya.backend.DTO.jobs.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.enums.ProjectLength;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobPostingDTO {

    private String title;
    private List<Skill> skills;
    private ProjectLength projectLength;
    private ExperienceLevel experienceLevel;
    private float expectedPricePerHour;
    private String description;


}
