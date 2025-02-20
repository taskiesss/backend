package taskaya.backend.DTO.freelancers.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvrHoursPerWeekUpdateRequestDTO {
    Double avrgHoursPerWeek;
}
