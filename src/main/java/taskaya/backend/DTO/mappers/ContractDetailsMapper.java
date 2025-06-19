package taskaya.backend.DTO.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.contracts.responses.ContractDetailsResponseDTO;

import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Milestone;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.services.community.CommunityService;
import taskaya.backend.services.community.VoteService;

import java.util.List;

@Component
public class ContractDetailsMapper {


    private static VoteService voteService;


    private static CommunityService communityService;

    @Autowired
    public void setCommunityService(CommunityService communityService) {
        ContractDetailsMapper.communityService = communityService;
    }
    @Autowired
    public void setVoteService(VoteService voteService) {
        ContractDetailsMapper.voteService = voteService;
    }
    public static ContractDetailsResponseDTO toDTO(Contract contract, String freelancerName,
                                                   String freelancerPicture, String freelancerId,Double memberPercentage){
        Integer hoursWorked = contract.getMilestones().stream()
                .filter(m -> m.getStatus() == Milestone.MilestoneStatus.APPROVED)
                .mapToInt(Milestone::getEstimatedHours)
                .sum();
        Double totalEarnings = contract.getCostPerHour()* hoursWorked;

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
                .memberPercentage(memberPercentage*100.0)
                .memberEarnings(memberPercentage*totalEarnings)
                .totalCurrentEarnings(totalEarnings)
                .isCommunity(contract.getWorkerEntity().getType()==WorkerEntity.WorkerType.COMMUNITY)
                .build();

        boolean isCommunityAdmin = true; //true if the freelancer is the owner of the community or the owner of the contract
        if (contract.getWorkerEntity().getType() == WorkerEntity.WorkerType.COMMUNITY) {
            if (contract.getStatus() == Contract.ContractStatus.PENDING) {
                responseDTO.setVoteIsDone(voteService.isVoteDone(contract));
            } else if (contract.getStatus()== Contract.ContractStatus.ENDED) {
                isCommunityAdmin = communityService.isUserCommunityAddmin(communityService.getCommunityByWorkerEntity(contract.getWorkerEntity()));
            }
        }
        if (contract.getStatus()== Contract.ContractStatus.ENDED){
            responseDTO.setPendingClientToRate(contract.getClientRatingForFreelancer() == null
                    || contract.getClientRatingForFreelancer()==0);
            responseDTO.setPendingFreelancerToRate((contract.getFreelancerRatingForClient() == null
                    || contract.getFreelancerRatingForClient()==0)&& isCommunityAdmin);
        }
        return responseDTO;
    }
}
