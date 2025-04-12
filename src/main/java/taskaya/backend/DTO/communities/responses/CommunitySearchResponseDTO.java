package taskaya.backend.DTO.communities.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.entity.enums.ExperienceLevel;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunitySearchResponseDTO {
    private UUID id;
    private String title;
    private String name;
    private String description;
    private ExperienceLevel experienceLevel;
    Integer avrgHoursPerWeek;
    private List<String> skills;
    private int memberCount;
    private double pricePerHour;
    private float rate;
    String profilePicture;
    Boolean isFull;
}
