package taskaya.backend.DTO.proposals.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.DTO.clients.SimpleJobClientResponseDTO;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.enums.ProjectLength;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDetailsResponseDTO {
    private String projectTitle;
    private Date postedAt;
    private String projectDescription;
    private ProjectLength projectLength;
    private ExperienceLevel experienceLevel;
    private Double pricePerHour;
    private List<String> skills;
    private boolean canApply;
    private SimpleJobClientResponseDTO client;
}

