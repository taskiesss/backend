package taskaya.backend.DTO.search.freelancers;

import lombok.Builder;
import lombok.Data;
import taskaya.backend.entity.Skill;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class FreelancerSearchResponseDTO {
    UUID id ;
    String name;
    String title ;
    String description;
    List<Skill>skills;
    float rate ;
    double hourlySalary;

}
