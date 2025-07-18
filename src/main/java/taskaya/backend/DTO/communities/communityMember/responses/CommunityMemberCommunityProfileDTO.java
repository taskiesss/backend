package taskaya.backend.DTO.communities.communityMember.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityMemberCommunityProfileDTO {
    String freelancerId;
    String freelancerProfilePicture;
    String name;
    String position;
    @Builder.Default
    boolean admin = false ;
}
