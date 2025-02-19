package taskaya.backend.DTO.freelancers.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.entity.freelancer.Education;
import taskaya.backend.entity.freelancer.EmployeeHistory;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreelancerProfileDTO {
    private String uuid;
    private String profilePicture;
    private String name;
    private String username;
    private String country;
    private Double pricePerHour;
    private float rate;
    private List<String> skills;
    private Double avrgHoursPerWeek;
    private List<String> languages;
    private List<Education> educations;
    private String linkedIn;
    private String description;
    private List<EmployeeHistory> employeeHistory;
}
