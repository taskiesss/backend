package taskaya.backend.DTO.communities.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberForCreateCommunityDTO {
    private String positionName;
    private Float  percentage;
    private String description;

}
