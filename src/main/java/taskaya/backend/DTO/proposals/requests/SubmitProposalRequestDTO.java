package taskaya.backend.DTO.proposals.requests;

import lombok.Builder;
import lombok.Data;
import taskaya.backend.DTO.milestones.requests.MilestoneSubmitProposalRequestDTO;
import taskaya.backend.entity.enums.Payment;
import java.util.List;

@Data
@Builder
public class SubmitProposalRequestDTO {
    String jobId;
    String candidateId;
    double pricePerHour;
    Payment freelancerPayment;
    List<MilestoneSubmitProposalRequestDTO> milestones;
    String coverLetter;
    String attachment;
}
