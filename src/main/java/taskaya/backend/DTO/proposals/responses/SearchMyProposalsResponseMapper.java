package taskaya.backend.DTO.proposals.responses;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import taskaya.backend.entity.work.Proposal;

import java.util.LinkedList;
import java.util.List;


public class SearchMyProposalsResponseMapper {
    public static SearchMyProposalsResponseDTO toDTO(Proposal proposal ) {
        return SearchMyProposalsResponseDTO.builder()
                .proposalId(proposal.getId().toString())
                .clientName(proposal.getClient().getName())
                .clientId(proposal.getClient().getId().toString())
                .jobName(proposal.getJob().getTitle())
                .jobId(proposal.getJob().getUuid().toString())
                .status(proposal.getStatus())
                .contractId(proposal.getContract() != null ? proposal.getContract().getId().toString() : null)
                .build();
    }

    public static List<SearchMyProposalsResponseDTO> toDTOList(List<Proposal> proposals){

        List<SearchMyProposalsResponseDTO> result = new LinkedList<>();
        for (Proposal proposal :proposals){
            result.add(toDTO(proposal));
        }
        return result;
    }

    public static Page<SearchMyProposalsResponseDTO> toDTOPage(Page<Proposal> proposals){
        return new PageImpl<>(toDTOList(proposals.getContent()), proposals.getPageable(), proposals.getTotalElements());
    }

}
