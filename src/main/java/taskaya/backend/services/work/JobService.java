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

        // Sorting logic
        Sort sort = Sort.by(Sort.Direction.ASC, "postedAt"); // Default sorting by date
        if (request.getSortBy() != null) {
            Sort.Direction direction = request.getSortDirection() == SortDirection.DESC ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, request.getSortBy().getValue());
        }

        // Pagination logic
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page <Job> jobPage = jobRepository.findAllByAssignedToIsNull(spec, pageable);

        return JobSearchResponseMapper.toDTOPage(jobPage);
    }
}

