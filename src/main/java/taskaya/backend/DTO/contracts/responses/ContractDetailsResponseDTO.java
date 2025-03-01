package taskaya.backend.DTO.contracts.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import taskaya.backend.DTO.milestones.responses.MilestonesContractDetailsResponseDTO;
import taskaya.backend.entity.enums.Payment;
import taskaya.backend.entity.work.Contract;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContractDetailsResponseDTO {
    Contract.ContractStatus contractStatus;
    String jobId;
    String jobTitle;
    String clientName;
    String clientProfilePic;
    String freelancerName;
    String freelancerProfilePic;
    Double pricePerHour;
    Double totalCurrentEarnings;
    Integer hoursWorked;
    Date startDate;
    Date endDate;
    Boolean isCommunity;
    Payment projectType;
}
