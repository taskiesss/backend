package taskaya.backend.DTO.contracts.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import taskaya.backend.DTO.milestones.responses.MilestonesContractDetailsResponseDTO;
import taskaya.backend.entity.enums.PaymentMethod;
import taskaya.backend.entity.work.Contract;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContractDetailsResponseDTO {
    Contract.ContractStatus contractStatus;
    String jobId;
    String jobTitle;
    String clientId;
    String clientName;
    String clientProfilePic;
    String freelancerId;
    String freelancerName;
    String freelancerProfilePic;
    Double pricePerHour;
    Double totalCurrentEarnings;
    Integer hoursWorked;
    Date startDate;
    Date endDate;
    Boolean isCommunity;
    @Builder.Default
    Boolean isCommunityAdmin= false;
    PaymentMethod projectType;
    Double memberPercentage;
    Double memberEarnings;
    @Builder.Default
    boolean voteIsDone=false;
    @Builder.Default
    boolean pendingClientToRate = false;
    @Builder.Default
    boolean pendingFreelancerToRate = false;

}
