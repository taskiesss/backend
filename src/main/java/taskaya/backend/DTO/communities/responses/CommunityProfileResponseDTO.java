package taskaya.backend.DTO.communities.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.DTO.communities.communityMember.responses.CommunityMemberCommunityProfileDTO;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.CommunityMember;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityProfileResponseDTO {
    String profilePicture;
    String name;
    String title;
    String country;
    String coverPhoto;
    double pricePerHour;
    float rate;
    String description;
    List<CommunityMemberCommunityProfileDTO> communityMembers;
    List<String> skills;
    Boolean isFull;
    Boolean isMember;
    Boolean isAdmin;
}
