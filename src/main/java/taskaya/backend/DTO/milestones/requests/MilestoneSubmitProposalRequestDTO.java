package taskaya.backend.DTO.milestones.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MilestoneSubmitProposalRequestDTO {
    String title;
    String description;
    Date dueDate;
    Integer expectedHours;
    Integer milestoneNumber;
}
