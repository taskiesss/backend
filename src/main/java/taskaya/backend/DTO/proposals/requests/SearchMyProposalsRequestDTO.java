package taskaya.backend.DTO.proposals.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.entity.work.Proposal;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchMyProposalsRequestDTO {
    private Integer page= 0;
    private Integer size= 10;

    private String search ;
    private List<Proposal.ProposalStatus> status ;
    private String jobId;
}
