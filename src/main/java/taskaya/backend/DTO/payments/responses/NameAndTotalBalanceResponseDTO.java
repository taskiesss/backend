package taskaya.backend.DTO.payments.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NameAndTotalBalanceResponseDTO {
    private String profilePicture;
    private String name;
    private Double totalBalance;
}
