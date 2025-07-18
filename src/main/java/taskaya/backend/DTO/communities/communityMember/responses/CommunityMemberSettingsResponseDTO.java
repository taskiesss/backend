package taskaya.backend.DTO.communities.communityMember.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityMemberSettingsResponseDTO {
    Boolean isUserAdmin;
    List<CommunityMemberPositionAndRoleDTO> communityMembers;
}
