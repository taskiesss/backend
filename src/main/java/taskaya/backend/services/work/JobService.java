package taskaya.backend.services.work;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.mappers.JobSearchResponseMapper;
import taskaya.backend.DTO.jobs.requests.JobSearchRequestDTO;
import taskaya.backend.DTO.jobs.responses.JobSearchResponseDTO;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.work.Job;
import taskaya.backend.repository.work.JobRepository;
import taskaya.backend.specifications.JobSpecification;



@Service
public class JobService {

    @Autowired
    private  JobRepository jobRepository;

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
        }else {
            pageable=PageRequest.of(request.getPage(), request.getSize());
        }


        Page <Job> jobPage = jobRepository.findAll(spec, pageable);

        return JobSearchResponseMapper.toDTOPage(jobPage);
    }
}

