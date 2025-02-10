package taskaya.backend.DTO.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.freelancer.Education;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FirstTimeFreelancerFormDTO {
    private String firstName;
    private String lastName;
    private String professionalTitle;
    private List<Skill> skills;
    private Double hourlyRate;
    private String professionalSummary;
    private List<Education> education;
    private List<String> languages;
    private Double hoursPerWeek;
}
