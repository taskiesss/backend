package taskaya.backend.DTO.freelancers.responses;

import lombok.Builder;
import lombok.Data;
import taskaya.backend.entity.enums.ExperienceLevel;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class FreelancerSearchResponseDTO {
    UUID id ;
    String name;
    String title ;
    ExperienceLevel experienceLevel;
    String description;
    List<String>skills;
    float rate ;
    double pricePerHour;
    String profilePicture;

}
