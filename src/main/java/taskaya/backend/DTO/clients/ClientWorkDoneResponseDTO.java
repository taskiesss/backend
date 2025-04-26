package taskaya.backend.DTO.clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientWorkDoneResponseDTO {
    private String jobName;
    private String jobId;
    private Float rate;
    private Double pricePerHour;
    private Double totalHours;
}
