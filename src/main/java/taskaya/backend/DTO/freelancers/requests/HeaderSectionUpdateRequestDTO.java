package taskaya.backend.DTO.freelancers.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeaderSectionUpdateRequestDTO {
    private String firstName;
    private String jobTitle;
    private Integer avgHoursPerWeek;
    private Float pricePerHour;
}
