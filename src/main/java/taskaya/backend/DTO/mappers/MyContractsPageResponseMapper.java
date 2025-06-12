package taskaya.backend.DTO.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.contracts.responses.MyContractsPageResponseDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerSearchResponseDTO;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Milestone;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.repository.work.ContractRepository;
import taskaya.backend.services.work.ContractService;

import java.util.LinkedList;
import java.util.List;


public class MyContractsPageResponseMapper {

    @Autowired
    static FreelancerRepository freelancerRepository;
    @Autowired
    static CommunityRepository communityRepository;


    public static MyContractsPageResponseDTO toDTO(Contract contract,boolean isClient){
        String clientNameOrUsername = (contract.getClient().getName()==null || contract.getClient().getName().isEmpty() )?
                contract.getClient().getUser().getUsername() : contract.getClient().getName();

        Milestone activeMiletone = ContractService.getActiveMilestone(contract);
        String profilePicture;

        if (isClient) {
            profilePicture = contract.getClient().getProfilePicture();
        } else {
            WorkerEntity worker = contract.getWorkerEntity();
            if (contract.getWorkerEntity().getType() == WorkerEntity.WorkerType.FREELANCER) {
                profilePicture = freelancerRepository.findByWorkerEntity(worker)
                        .map(Freelancer::getProfilePicture)
                        .orElse(null);
            } else {
                profilePicture = communityRepository.findByWorkerEntity(worker)
                        .map(Community::getProfilePicture)
                        .orElse(null);
            }
        }

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
                .profilePicture(profilePicture)
                .build();
    }

    public static List<MyContractsPageResponseDTO> toDTOList(List<Contract> contracts, boolean isClient){

        List<MyContractsPageResponseDTO> result = new LinkedList<>();
        for (Contract contract :contracts){
            result.add(toDTO(contract,isClient));
        }
        return result;
    }

    public static Page<MyContractsPageResponseDTO> toDTOPage(Page<Contract> contracts,boolean isClient){
        return new PageImpl<>(toDTOList(contracts.getContent(), isClient), contracts.getPageable(), contracts.getTotalElements());
    }

}
