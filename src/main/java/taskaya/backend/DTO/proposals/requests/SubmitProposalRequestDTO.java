package taskaya.backend.DTO.proposals.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import taskaya.backend.DTO.milestones.requests.MilestoneSubmitRequestDTO;
import taskaya.backend.entity.enums.PaymentMethod;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitProposalRequestDTO {
    String jobId;
    String candidateId;
    double pricePerHour;
    PaymentMethod freelancerPayment;
    List<MilestoneSubmitRequestDTO> milestones;
    String coverLetter;
    MultipartFile attachment;
}
