package taskaya.backend.DTO.contracts.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.DTO.milestones.requests.MilestoneSubmitRequestDTO;
import taskaya.backend.entity.enums.PaymentMethod;

import java.util.Date;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateContractRequestDTO {

    String description;
    double costPerHour;
    Date startDate;
    PaymentMethod payment;
    List<MilestoneSubmitRequestDTO> milestones;


}
