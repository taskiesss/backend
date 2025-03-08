package taskaya.backend.DTO.payments.responses;

import lombok.Builder;
import lombok.Data;
import taskaya.backend.entity.Payment;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class PaymentSearchResponseDTO {
    private UUID id;
    private Date date;
    private Payment.Type type;
    private String description;
    private double amount;
    private UUID contractId;
}
