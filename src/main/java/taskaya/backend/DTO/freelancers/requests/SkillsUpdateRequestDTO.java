package taskaya.backend.DTO.freelancers.requests;

import lombok.Builder;
import lombok.Data;
import taskaya.backend.entity.Skill;

import java.util.List;

@Data
@Builder
public class SkillsUpdateRequestDTO {
    List<Skill> skills;
}
