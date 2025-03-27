package taskaya.backend.DTO.communities.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.DTO.communities.communityMember.responses.CommunityMemberCommunityProfileDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityVotesDetailsResponseDTO {
    List<CommunityMemberCommunityProfileDTO> accepted;
    List<CommunityMemberCommunityProfileDTO> rejected;
    List<CommunityMemberCommunityProfileDTO> remaining;
}
