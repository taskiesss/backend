package taskaya.backend.DTO.search.communities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.entity.Skill;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunitySearchResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private List<String> skills;
    private int memberCount;
    private double pricePerHour;
    private float rate;
    String profilePicture;
    Boolean isFull;
}
