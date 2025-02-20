package taskaya.backend.DTO.mappers;


import org.springframework.stereotype.Component;
import taskaya.backend.DTO.freelancers.responses.FreelancerWorkdoneResponseDTO;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.Milestone;

import java.util.LinkedList;
import java.util.List;

@Component
public class FreelancerWorkdoneResponseMapper {
    public static FreelancerWorkdoneResponseDTO toDTO(Job job){
        List<Milestone> milestones = job.getContract().getMilestones();

        double totalHours = 0;
        for(Milestone milestone : milestones){
            totalHours += milestone.getEstimatedHours();
        }

        return FreelancerWorkdoneResponseDTO.builder()
                .id(job.getUuid())
                .jobName(job.getTitle())
                .rate(job.getRate())
                .pricePerHour(job.getContract().getCostPerHour())
                .totalHours(totalHours)
                .build();
    }

    public static List<FreelancerWorkdoneResponseDTO> toDTOList(List<Job> jobs){
        List<FreelancerWorkdoneResponseDTO> result = new LinkedList<>();
        for (Job job :jobs){
            result.add(toDTO(job));
        }
        return result;
    }

}
