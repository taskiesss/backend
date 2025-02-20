package taskaya.backend.DTO.freelancers.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FreelancerWorkdoneResponseDTO {
    UUID id;
    String jobName;
    double rate;
    double pricePerHour;
    double totalHours;
}
