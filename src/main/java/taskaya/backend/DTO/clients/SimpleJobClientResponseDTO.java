package taskaya.backend.DTO.clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleJobClientResponseDTO {
    private Integer completedJobs;
    private String totalSpent;
    private Double rate;
}
