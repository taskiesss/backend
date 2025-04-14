package taskaya.backend.DTO.freelancers.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FreelancerIsMemberOrAdminOfResponseDTO {
    private String communityId;
    private String name;
    private String profilePicture;
    private String communityTitle;

}
