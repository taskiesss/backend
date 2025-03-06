package taskaya.backend.DTO.communities.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityJoinReqResponseDTO {
    private UUID freelancerID;
    private String name;
    private String profilePicture;
    private String positionName;
}
