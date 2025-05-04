package taskaya.backend.controller.work;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskaya.backend.DTO.SimpleResponseDTO;
import taskaya.backend.DTO.jobs.requests.JobPostingDTO;
import taskaya.backend.DTO.jobs.requests.JobSearchRequestDTO;
import taskaya.backend.DTO.jobs.responses.JobSearchResponseDTO;
import taskaya.backend.DTO.proposals.responses.JobDetailsResponseDTO;
import taskaya.backend.services.work.JobService;


@RestController
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping("/jobs/search")
    public ResponseEntity<Page<JobSearchResponseDTO>> searchJobs(@RequestBody JobSearchRequestDTO request) {
        Page<JobSearchResponseDTO> jobs = jobService.searchJobs(request);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("api/jobs/{id}")
    public ResponseEntity<JobDetailsResponseDTO> getJobDetails(@PathVariable String id){
        JobDetailsResponseDTO jobDetailsResponseDTO = jobService.getJobDetails(id);
        return ResponseEntity.ok(jobDetailsResponseDTO);
    }

    @PostMapping("/clients/PostJob")
    public ResponseEntity<?> postJob(@RequestBody JobPostingDTO request) {
        jobService.postJob(request);
        return ResponseEntity.ok(SimpleResponseDTO.builder().message("true").build());
    }
}


