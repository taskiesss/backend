package taskaya.backend.controller.work;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskaya.backend.DTO.search.jobs.JobSearchRequestDTO;
import taskaya.backend.entity.work.Job;
import taskaya.backend.services.work.JobService;


@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping("/search")
    public ResponseEntity<Page<Job>> searchJobs(@RequestBody JobSearchRequestDTO request) {
        Page<Job> jobs = jobService.searchJobs(request);
        return ResponseEntity.ok(jobs);
    }
}


