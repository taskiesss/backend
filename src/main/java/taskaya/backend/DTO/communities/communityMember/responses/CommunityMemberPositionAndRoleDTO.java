package taskaya.backend.DTO.communities.communityMember.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityMemberPositionAndRoleDTO {
    CommunityMemberCommunityProfileDTO nameAndPicture;
    String description;
    Long positionId;
    Double currentPercentage;
    Integer futurePercentage;
}
