package taskaya.backend.DTO.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.contracts.responses.ContractDetailsResponseDTO;
import taskaya.backend.DTO.milestones.responses.MilestonesContractDetailsResponseDTO;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Milestone;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.services.community.VoteService;

import java.util.List;

@Component
public class ContractDetailsMapper {


    private static VoteService voteService;


    @Autowired
    public void setVoteService(VoteService voteService) {
        ContractDetailsMapper.voteService = voteService;
    }
    public static ContractDetailsResponseDTO toDTO(Contract contract, String freelancerName,
                                                   String freelancerPicture, String freelancerId){
        Integer hoursWorked = contract.getMilestones().stream()
                .filter(m -> m.getStatus() == Milestone.MilestoneStatus.APPROVED)
                .mapToInt(Milestone::getEstimatedHours)
                .sum();

        ContractDetailsResponseDTO responseDTO = ContractDetailsResponseDTO.builder()
                .jobId(contract.getJob().getUuid().toString())
                .pricePerHour(contract.getCostPerHour())
                .contractStatus(contract.getStatus())
                .clientId(contract.getClient().getId().toString())
                .clientName(contract.getClient().getName())
                .clientProfilePic(contract.getClient().getProfilePicture())
                .freelancerId(freelancerId)
                .freelancerName(freelancerName)
                .freelancerProfilePic(freelancerPicture)
                .jobTitle(contract.getJob().getTitle())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .projectType(contract.getPayment())
                .hoursWorked(hoursWorked)
                .totalCurrentEarnings(contract.getCostPerHour()* hoursWorked)
                .isCommunity(contract.getWorkerEntity().getType()==WorkerEntity.WorkerType.COMMUNITY)
                .build();
        if (contract.getWorkerEntity().getType() == WorkerEntity.WorkerType.COMMUNITY
                &&contract.getStatus()== Contract.ContractStatus.PENDING){
            responseDTO.setVoteIsDone(voteService.isVoteDone(contract));
        }
        if (contract.getStatus()== Contract.ContractStatus.ENDED){
            responseDTO.setPendingClientToRate(contract.getClientRatingForFreelancer() == null
                    || contract.getClientRatingForFreelancer()==0);
            responseDTO.setPendingFreelancerToRate(contract.getFreelancerRatingForClient() == null
                    || contract.getFreelancerRatingForClient()==0);
        }
        return responseDTO;
    }
}
