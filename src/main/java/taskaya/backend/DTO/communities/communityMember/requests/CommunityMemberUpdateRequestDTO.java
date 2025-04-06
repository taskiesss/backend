package taskaya.backend.DTO.communities.communityMember.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityMemberUpdateRequestDTO {
     Long positionId;
     String positionName;
     Integer financialPercent;
     String description;
}
