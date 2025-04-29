package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.proposals.responses.ProposalDetailsResponseDTO;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Milestone;
import taskaya.backend.entity.work.Proposal;

@Component
public class ProposalDetailsResponseMapper {
    public static ProposalDetailsResponseDTO toDTO(Proposal proposal, Freelancer freelancer) {

        Integer hoursWorked = proposal.getMilestones().stream()
                .filter(m -> m.getStatus() == Milestone.MilestoneStatus.APPROVED)
                .mapToInt(Milestone::getEstimatedHours)
                .sum();
        return ProposalDetailsResponseDTO.builder()
                .proposalId(proposal.getId().toString())
                .jobName(proposal.getJob().getTitle())
                .freelancerName(freelancer.getName())
                .freelancerId(freelancer.getId().toString())
                .profilePicture(freelancer.getProfilePicture())
                .isCommunity(Boolean.FALSE)
                .coverLetter(proposal.getCoverLetter())
                .pricePerHour(proposal.getCostPerHour())
                .paymentMethod(proposal.getPayment())
                .attachment(proposal.getAttachment())
                .date(proposal.getDate())
                .status(proposal.getStatus())
                .totalHours(hoursWorked)
                .build();
    }
    public static ProposalDetailsResponseDTO toDTO(Proposal proposal, Community community) {

        return ProposalDetailsResponseDTO.builder()
                .proposalId(proposal.getId().toString())
                .jobName(proposal.getJob().getTitle())
                .freelancerName(community.getCommunityName())
                .freelancerId(community.getUuid().toString())
                .profilePicture(community.getProfilePicture())
                .isCommunity(Boolean.TRUE)
                .coverLetter(proposal.getCoverLetter())
                .pricePerHour(proposal.getCostPerHour())
                .paymentMethod(proposal.getPayment())
                .attachment(proposal.getAttachment())
                .date(proposal.getDate())
                .status(proposal.getStatus())
                .build();
    }
}
