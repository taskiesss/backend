package taskaya.backend.DTO.mappers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.clients.ClientPostedJobsResponseDTO;
import taskaya.backend.DTO.communities.responses.CommunityJoinReqResponseDTO;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Job;

import java.util.LinkedList;
import java.util.List;

@Component
public class ClientPostedJobsResponseMapper {
    public static ClientPostedJobsResponseDTO toDTO(Job job){

        Integer hired;
        String contractId;

        if(job.getAssignedTo() == null){
            hired =0;
            contractId = null;
        }else {
            hired = 1;
            contractId = job.getContract().getId().toString();
        }

        return ClientPostedJobsResponseDTO.builder()
                .jobName(job.getTitle())
                .jobId(job.getUuid().toString())
                .postedAt(job.getPostedAt())
                .hired(hired)
                .contractId(contractId)
                .build();
    }

    public static List<ClientPostedJobsResponseDTO> toDTOList(List<Job> jobs){

        List<ClientPostedJobsResponseDTO> result = new LinkedList<>();
        for (Job job :jobs){
            result.add(toDTO(job));
        }
        return result;
    }

    public static Page<ClientPostedJobsResponseDTO> toDTOPage(Page<Job> jobs){
        return new PageImpl<>(toDTOList(jobs.getContent()), jobs.getPageable(), jobs.getTotalElements());
    }
}
