package taskaya.backend.DTO.mappers;


import org.springframework.stereotype.Component;
import taskaya.backend.DTO.workerEntity.responses.WorkerEntityWorkdoneResponseDTO;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.Milestone;

import java.util.LinkedList;
import java.util.List;

@Component
public class WorkerEntityWorkdoneResponseMapper {
    public static WorkerEntityWorkdoneResponseDTO toDTO(Job job){
        List<Milestone> milestones = job.getContract().getMilestones();

        double totalHours = 0;
        for(Milestone milestone : milestones){
            totalHours += milestone.getEstimatedHours();
        }

        return WorkerEntityWorkdoneResponseDTO.builder()
                .jobId(job.getUuid().toString())
                .jobName(job.getTitle())
                .rate(job.getContract().getClientRatingForFreelancer())
                .pricePerHour(job.getContract().getCostPerHour())
                .totalHours(totalHours)
                .build();
    }

    public static List<WorkerEntityWorkdoneResponseDTO> toDTOList(List<Job> jobs){
        List<WorkerEntityWorkdoneResponseDTO> result = new LinkedList<>();
        for (Job job :jobs){
            result.add(toDTO(job));
        }
        return result;
    }

}
