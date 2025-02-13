package taskaya.backend.services.work;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.clients.SimpleJobClientResponseDTO;
import taskaya.backend.DTO.mappers.JobDetailsResponseMapper;
import taskaya.backend.DTO.mappers.JobSearchResponseMapper;
import taskaya.backend.DTO.jobs.requests.JobSearchRequestDTO;
import taskaya.backend.DTO.jobs.responses.JobSearchResponseDTO;
import taskaya.backend.DTO.mappers.SimpleJobClientResponseMapper;
import taskaya.backend.DTO.proposals.responses.JobDetailsResponseDTO;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.work.Job;
import taskaya.backend.exceptions.notFound.NotFoundException;
import taskaya.backend.repository.work.JobRepository;
import taskaya.backend.specifications.JobSpecification;

import java.util.UUID;


@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    public Page<JobSearchResponseDTO> searchJobs(JobSearchRequestDTO request) {
        Specification<Job> spec = JobSpecification.filterJobs(request);

        Pageable pageable;

        if (request.getSortBy() != null) {
            String sortField = request.getSortBy().getValue().equals("rate")
                    ? "client.rate"
                    : request.getSortBy().getValue();

            Sort sort;
            if (SortDirection.DESC.equals(request.getSortDirection())) {

                sort = Sort.by(Sort.Order.desc(sortField));
            } else {
                sort = Sort.by(Sort.Order.asc(sortField));
            }

            // Create Page Request for pagination
            pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        } else {
            pageable = PageRequest.of(request.getPage(), request.getSize());
        }


        Page<Job> jobPage = jobRepository.findAll(spec, pageable);

        return JobSearchResponseMapper.toDTOPage(jobPage);
    }

    public JobDetailsResponseDTO getJobDetails (String jobId){
        Job job = jobRepository.findById(UUID.fromString(jobId))
                .orElseThrow(()->new NotFoundException("Job not found."));
        Client client = job.getClient();

        SimpleJobClientResponseDTO jobClientResponseDTO = SimpleJobClientResponseMapper.toDTO(client);
        return JobDetailsResponseMapper.toDTO(job,jobClientResponseDTO);
    }
}