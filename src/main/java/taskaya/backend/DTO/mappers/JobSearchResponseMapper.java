package taskaya.backend.DTO.mappers;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.jobs.responses.JobSearchResponseDTO;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.work.Job;

import java.util.LinkedList;
import java.util.List;

@Component
public class JobSearchResponseMapper {

    public static JobSearchResponseDTO toDTO(Job job) {
        return JobSearchResponseDTO.builder()
                .id(job.getUuid())
                .title(job.getTitle())
                .description((job.getDescription()!= null &&job.getDescription().length()>256)?job.getDescription().substring(0,256)+"...": job.getDescription())
                .experienceLevel(job.getExperienceLevel())
                .skills(job.getSkills().stream().map(Skill::getName).toList())
                .pricePerHour(job.getPricePerHour())
                .postedDate(job.getPostedAt())
                .projectLength(job.getProjectLength())
                .rate(job.getClient().getRate())
                .build();
    }

    public  static List<JobSearchResponseDTO> toDTOList(List<Job>jobs) {
        List<JobSearchResponseDTO> result = new LinkedList<>();
        for(Job job : jobs){
            result.add(toDTO(job));
        }
        return result;
    }

    public static Page<JobSearchResponseDTO> toDTOPage(Page<Job> jobs){
        return new PageImpl<>(toDTOList(jobs.getContent()),jobs.getPageable(), jobs.getTotalElements());
    }
}
