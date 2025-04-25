package taskaya.backend.DTO.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.contracts.responses.MyContractsPageResponseDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerSearchResponseDTO;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Milestone;
import taskaya.backend.repository.work.ContractRepository;
import taskaya.backend.services.work.ContractService;

import java.util.LinkedList;
import java.util.List;


public class MyContractsPageResponseMapper {



    public static MyContractsPageResponseDTO toDTO(Contract contract){
        String clientNameOrUsername = (contract.getClient().getName()==null || contract.getClient().getName().isEmpty() )?
                contract.getClient().getUser().getUsername() : contract.getClient().getName();

        Milestone activeMiletone = ContractService.getActiveMilestone(contract);

        return MyContractsPageResponseDTO.builder()
                .contractID(contract.getId())
                .jobID(contract.getJob().getUuid())
                .jobTitle(contract.getJob().getTitle())
                .clientName(clientNameOrUsername)
                .clientID(contract.getClient().getId())
                .contractStatus(contract.getStatus())
                .budget(ContractService.getContractBudget(contract))
                .activeMilestone(activeMiletone==null?null: activeMiletone.getName())
                .clientRateForFreelancer(contract.getClientRatingForFreelancer())
                .freelancerRateForClient(contract.getFreelancerRatingForClient())
                .startDate(contract.getStartDate())
                .dueDate(contract.getDueDate())
                .endDate(contract.getEndDate())
                .build();
    }

    public static List<MyContractsPageResponseDTO> toDTOList(List<Contract> contracts){

        List<MyContractsPageResponseDTO> result = new LinkedList<>();
        for (Contract contract :contracts){
            result.add(toDTO(contract));
        }
        return result;
    }

    public static Page<MyContractsPageResponseDTO> toDTOPage(Page<Contract> contracts){
        return new PageImpl<>(toDTOList(contracts.getContent()), contracts.getPageable(), contracts.getTotalElements());
    }

}
