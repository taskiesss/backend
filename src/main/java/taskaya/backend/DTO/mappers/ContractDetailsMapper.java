package taskaya.backend.DTO.mappers;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.contracts.responses.ContractDetailsResponseDTO;
import taskaya.backend.DTO.milestones.responses.MilestonesContractDetailsResponseDTO;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Milestone;
import taskaya.backend.entity.work.WorkerEntity;

import java.util.List;

@Component
public class ContractDetailsMapper {
    public static ContractDetailsResponseDTO toDTO(Contract contract, String freelancerName,
                                                   String freelancerPicture, String freelancerId,Double memberPercentage){
        Integer hoursWorked = contract.getMilestones().stream()
                .filter(m -> m.getStatus() == Milestone.MilestoneStatus.APPROVED)
                .mapToInt(Milestone::getEstimatedHours)
                .sum();
        Double totalEarnings = contract.getCostPerHour()* hoursWorked;

        return ContractDetailsResponseDTO.builder()
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
                .memberPercentage(memberPercentage)
                .memberEarnings(memberPercentage*totalEarnings)
                .totalCurrentEarnings(totalEarnings)
                .isCommunity(contract.getWorkerEntity().getType()==WorkerEntity.WorkerType.COMMUNITY)
                .build();
    }
}
