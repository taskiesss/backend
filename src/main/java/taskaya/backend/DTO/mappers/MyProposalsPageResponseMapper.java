package taskaya.backend.DTO.mappers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.commons.responses.MyProposalsPageResponseDTO;
import taskaya.backend.entity.work.Proposal;

import java.util.LinkedList;
import java.util.List;

@Component
public class MyProposalsPageResponseMapper {

    public static MyProposalsPageResponseDTO toDTO(Proposal proposal){
        return MyProposalsPageResponseDTO.builder()
                .date(proposal.getDate())
                .jobTitle(proposal.getJob().getTitle())
                .proposalId(proposal.getId().toString())
                .jobId(proposal.getJob().getUuid().toString())
                .status(proposal.getStatus())
                .contractId(proposal.getContract()==null?null:proposal.getContract().getId().toString())
                .build();
    }

    public static List<MyProposalsPageResponseDTO> toDTOList(List<Proposal> proposals){

        List<MyProposalsPageResponseDTO> result = new LinkedList<>();
        for (Proposal proposal :proposals){
            result.add(toDTO(proposal));
        }
        return result;
    }

    public static Page<MyProposalsPageResponseDTO> toDTOPage(Page<Proposal> proposals){
        return new PageImpl<>(toDTOList(proposals.getContent()), proposals.getPageable(), proposals.getTotalElements());
    }

}
