package taskaya.backend.DTO.contracts.responses;

import lombok.Builder;
import lombok.Data;
import taskaya.backend.entity.work.Contract;

import java.util.Date;
import java.util.UUID;

@Builder
@Data
public class MyContractsPageResponseDTO {
    private UUID contractID;
    private UUID jobID;
    private String jobTitle;
    private String clientName;
    private UUID clientID;
    private Contract.ContractStatus contractStatus;
    private Double budget;
    private String activeMilestone;
    private Float clientRateForFreelancer;
    private Date startDate;
    private Date dueDate;
    private Date endDate;
}
