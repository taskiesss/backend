package taskaya.backend.DTO.mappers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.communities.responses.CommunityJoinReqResponseDTO;
import taskaya.backend.DTO.communities.responses.CommunityOfferResponseDTO;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.community.JoinRequest;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Contract;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommunityOfferResponseMapper {
    public static CommunityOfferResponseDTO toDTO(Contract contract){

        return CommunityOfferResponseDTO.builder()
                .contractID(contract.getId().toString())
                .jobTitle(contract.getJob().getTitle())
                .description(contract.getDescription())
                .skills(contract.getJob().getSkills().stream().map(Skill::getName).toList())
                .pricePerHour(contract.getCostPerHour())
                .sentDate(contract.getSentDate())
                .dueDate(contract.getDueDate())
                .build();
    }

    public static List<CommunityOfferResponseDTO> toDTOList(List<Contract> contracts){

        List<CommunityOfferResponseDTO> result = new LinkedList<>();
        for (Contract contract :contracts){
            result.add(toDTO(contract));
        }
        return result;
    }

    public static Page<CommunityOfferResponseDTO> toDTOPage(Page<Contract> contracts){
            return new PageImpl<>(toDTOList(contracts.getContent()), contracts.getPageable(), contracts.getTotalElements());
    }
}
