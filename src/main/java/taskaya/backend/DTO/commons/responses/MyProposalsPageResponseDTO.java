package taskaya.backend.DTO.commons.responses;

import lombok.Builder;
import lombok.Data;
import taskaya.backend.entity.work.Proposal;

import java.util.Date;

@Data
@Builder
public class MyProposalsPageResponseDTO {
    private Date date ;
    private String jobTitle;
    private String proposalId;
    private String jobId;
    private Proposal.ProposalStatus status ;
    private String contractId;
}
