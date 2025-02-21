package taskaya.backend.DTO.freelancers.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeaderSectionUpdateRequestDTO {
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String country;
    private Double pricePerHour;
}
