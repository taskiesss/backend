package taskaya.backend.DTO.milestones.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.entity.work.Milestone;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MilestonesDetailsResponseDTO {
    String title;
    String description;
    Integer expectedHours;
    Date dueDate;
    Milestone.MilestoneStatus status;
    String milestoneId;
}
