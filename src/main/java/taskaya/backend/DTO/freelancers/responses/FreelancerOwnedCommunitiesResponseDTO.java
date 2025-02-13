package taskaya.backend.DTO.freelancers.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FreelancerOwnedCommunitiesResponseDTO {
    private String id;
    private String name;
}
