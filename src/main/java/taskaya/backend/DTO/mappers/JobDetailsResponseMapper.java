package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.clients.SimpleJobClientResponseDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerSearchResponseDTO;
import taskaya.backend.DTO.proposals.responses.JobDetailsResponseDTO;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Job;

@Component
public class JobDetailsResponseMapper {

    public static JobDetailsResponseDTO toDTO(Job job , SimpleJobClientResponseDTO clientResponseDTO){
        return JobDetailsResponseDTO.builder()
                .projectTitle(job.getTitle())
                .postedAt(job.getPostedAt())
                .projectDescription(job.getDescription())
                .projectLength(job.getProjectLength())
                .experienceLevel(job.getExperienceLevel())
                .pricePerHour(job.getPricePerHour())
                .skills(job.getSkills().stream().map(Skill::getName).toList())
                .client(clientResponseDTO)
                .build();
    }
}
