package taskaya.backend.DTO.milestones.requests;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class MilestoneSubmitProposalRequestDTO {
    String title;
    String description;
    Date dueDate;
    Integer expectedHours;
    Integer milestoneNumber;
}
