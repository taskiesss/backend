package taskaya.backend.DTO.proposals.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.entity.enums.PaymentMethod;
import taskaya.backend.entity.work.Proposal;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProposalDetailsResponseDTO {
    private String proposalId;
    private String jobName;
    private String freelancerName;
    private String freelancerId;
    private String profilePicture;
    private Boolean isCommunity;
    private String coverLetter;
    private Double pricePerHour;
    private PaymentMethod paymentMethod;
    private String attachment;
    private Date date;
    private Proposal.ProposalStatus status;
}
