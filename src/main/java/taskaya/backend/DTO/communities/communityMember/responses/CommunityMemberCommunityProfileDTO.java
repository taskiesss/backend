package taskaya.backend.DTO.communities.communityMember.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityMemberCommunityProfileDTO {
    String name;
    String position;
}
