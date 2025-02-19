package taskaya.backend.DTO.freelancers.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.entity.freelancer.Education;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EducationsPatchRequestDTO {
    List<Education> educations;
}




