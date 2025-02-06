package taskaya.backend.DTO.search;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.community.Community;

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
    private List<Skill> skills;
    private int memberCount;
    private double pricePerHour;
    private float rate;
}
