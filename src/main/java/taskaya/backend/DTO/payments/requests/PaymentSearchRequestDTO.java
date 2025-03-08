package taskaya.backend.DTO.payments.requests;


import lombok.Data;
import taskaya.backend.entity.Payment;

import java.util.Date;

@Data
public class PaymentSearchRequestDTO {
    private Date startDate;
    private Date endDate;
    private int page;
    private int size;
    private Payment.Type type;
}