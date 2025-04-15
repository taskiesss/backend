package taskaya.backend.DTO.communities.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCommunityResponseDTO {
    private String communityID;
}
