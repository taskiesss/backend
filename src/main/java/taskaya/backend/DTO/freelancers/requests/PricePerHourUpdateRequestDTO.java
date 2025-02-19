package taskaya.backend.DTO.freelancers.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PricePerHourUpdateRequestDTO {
    private float pricePerHour;
}
