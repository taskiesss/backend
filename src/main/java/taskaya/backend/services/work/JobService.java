package taskaya.backend.services.work;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.mappers.JobSearchResponseMapper;
import taskaya.backend.DTO.search.jobs.JobSearchRequestDTO;
import taskaya.backend.DTO.search.jobs.JobSearchResponseDTO;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.work.Job;
import taskaya.backend.repository.work.JobRepository;
import taskaya.backend.specifications.JobSpecification;



@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public Page<JobSearchResponseDTO> searchJobs(JobSearchRequestDTO request) {
        Specification<Job> spec = JobSpecification.filterJobs(request);

        Pageable pageable;

        if (request.getSortBy() != null) {

            Sort sort;
            if (SortDirection.DESC.equals(request.getSortDirection())) {
                sort = Sort.by(Sort.Order.desc(request.getSortBy().getValue()));
            } else {
                sort = Sort.by(Sort.Order.asc(request.getSortBy().getValue()));
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

