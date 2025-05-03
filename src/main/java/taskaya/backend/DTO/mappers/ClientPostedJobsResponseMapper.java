package taskaya.backend.DTO.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.clients.ClientPostedJobsResponseDTO;
import taskaya.backend.entity.work.Job;
import taskaya.backend.repository.work.ProposalRepository;

import java.util.LinkedList;
import java.util.List;

@Component
public class ClientPostedJobsResponseMapper {

    private final ProposalRepository proposalRepository;

    @Autowired
    public ClientPostedJobsResponseMapper(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    public ClientPostedJobsResponseDTO toDTO(Job job) {
        Integer hired;
        String contractId;

        if (job.getAssignedTo() == null) {
            hired = 0;
            contractId = null;
        } else {
            hired = 1;
            contractId = job.getContract().getId().toString();
        }

        Integer proposalsCount = proposalRepository.countByJob(job);

        return ClientPostedJobsResponseDTO.builder()
                .jobName(job.getTitle())
                .jobId(job.getUuid().toString())
                .postedAt(job.getPostedAt())
                .hired(hired)
                .proposals(proposalsCount)
                .contractId(contractId)
                .build();
    }

    public List<ClientPostedJobsResponseDTO> toDTOList(List<Job> jobs) {
        List<ClientPostedJobsResponseDTO> result = new LinkedList<>();
        for (Job job : jobs) {
            result.add(toDTO(job));
        }
        return result;
    }

    public Page<ClientPostedJobsResponseDTO> toDTOPage(Page<Job> jobs) {
        return new PageImpl<>(toDTOList(jobs.getContent()), jobs.getPageable(), jobs.getTotalElements());
    }
}
