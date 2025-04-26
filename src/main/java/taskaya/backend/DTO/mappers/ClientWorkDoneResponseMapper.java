package taskaya.backend.DTO.mappers;

import taskaya.backend.DTO.clients.ClientWorkDoneResponseDTO;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.Milestone;

import java.util.LinkedList;
import java.util.List;

public class ClientWorkDoneResponseMapper {
    public static ClientWorkDoneResponseDTO toDTO(Job job ){
        List<Milestone> milestones = job.getContract().getMilestones();

        double totalHours = 0;
        for(Milestone milestone : milestones){
            totalHours += milestone.getEstimatedHours();
        }

        return ClientWorkDoneResponseDTO.builder()
                .jobId(job.getUuid().toString())
                .jobName(job.getTitle())
                .rate(job.getContract().getFreelancerRatingForClient())
                .pricePerHour(job.getPricePerHour())
                .totalHours(totalHours)
                .build();
    }

    public static List<ClientWorkDoneResponseDTO> toDTOList(List<Job> jobs) {
        List<ClientWorkDoneResponseDTO> result = new LinkedList<>();
        for (Job job :jobs){
            result.add(toDTO(job));
        }
        return result;
    }

}