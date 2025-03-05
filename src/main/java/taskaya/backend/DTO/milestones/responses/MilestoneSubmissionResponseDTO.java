package taskaya.backend.DTO.milestones.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.DTO.deliverables.responses.DeliverableFileSubmissionResponseDTO;
import taskaya.backend.DTO.deliverables.responses.DeliverableLinkSubmissionResponseDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MilestoneSubmissionResponseDTO {
    String description;
    List<DeliverableLinkSubmissionResponseDTO> links;
    List<DeliverableFileSubmissionResponseDTO> files;
}
