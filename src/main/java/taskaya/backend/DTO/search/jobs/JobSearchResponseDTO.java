package taskaya.backend.DTO.search.jobs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.enums.ProjectLength;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSearchResponseDTO {
    private UUID id;
    private String title;
    private String description;
    private ExperienceLevel experienceLevel;
    private List<String> skills;
    private double pricePerHour;
    private Date postedDate;
    private float rate;
    private boolean isSaved;
    private ProjectLength projectLength;
}
