package taskaya.backend.DTO.workerEntity.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkerEntityWorkdoneResponseDTO {
    String jobId;
    String jobName;
    Float rate;
    double pricePerHour;
    double totalHours;
}
