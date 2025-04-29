package taskaya.backend.DTO.proposals.responses;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.entity.work.Proposal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMyProposalsResponseDTO {

    private String proposalId;
    private String clientName;
    private String clientId;
    private String freelancerName;
    private String freelancerId;
    private String profilePicture;
    private String freelancerTitle;
    private String jobName;
    private String jobId;
    private boolean isCommunity;
    private Proposal.ProposalStatus status;
    private String contractId;
}