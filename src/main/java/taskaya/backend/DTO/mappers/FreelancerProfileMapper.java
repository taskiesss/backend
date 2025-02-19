package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.freelancers.responses.FreelancerProfileDTO;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.freelancer.Freelancer;

@Component
public class FreelancerProfileMapper {
    public static FreelancerProfileDTO toDTO(Freelancer freelancer){
        return FreelancerProfileDTO.builder()
                .uuid(freelancer.getId().toString())
                .profilePicture(freelancer.getProfilePicture())
                .name(freelancer.getName())
                .username(freelancer.getUser().getUsername())
                .country(freelancer.getCountry())
                .pricePerHour(freelancer.getPricePerHour())
                .rate(freelancer.getRate())
                .skills(freelancer.getSkills().stream().map(Skill::getName).toList())
                .avrgHoursPerWeek(freelancer.getFreelancerBusiness().getAvgHoursPerWeek())
                .languages(freelancer.getLanguages().stream().toList())
                .educations(freelancer.getEducations())
                .linkedIn(freelancer.getLinkedIn())
                .description(freelancer.getDescription())
                .employeeHistory(freelancer.getEmployeeHistories())
                .build();
    }
}
