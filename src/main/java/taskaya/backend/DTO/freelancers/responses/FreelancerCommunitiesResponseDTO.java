package taskaya.backend.DTO.freelancers.responses;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Data
@Builder
public class FreelancerCommunitiesResponseDTO {
    private ArrayList<FreelancerIsMemberOrAdminOfResponseDTO> adminOf;
    private ArrayList<FreelancerIsMemberOrAdminOfResponseDTO> memberOf;
}
