package taskaya.backend.controller;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskaya.backend.DTO.login.FirstTimeFreelancerFormDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerSearchResponseDTO;
import taskaya.backend.DTO.freelancers.requests.FreenlancerSearchRequestDTO;
import taskaya.backend.services.freelancer.FreelancerService;

@RestController
@RequestMapping("/freelancers")
public class FreelancerController {

    private final FreelancerService freelancerService;

    public FreelancerController(FreelancerService freelancerService) {
        this.freelancerService = freelancerService;
    }

    @PostMapping("/search")
    public ResponseEntity<Page<FreelancerSearchResponseDTO>> searchFreelancers(@RequestBody FreenlancerSearchRequestDTO request) {
        return ResponseEntity.ok(freelancerService.searchFreelancers(request));
    }

    @PostMapping("/freelancer-form")
    public ResponseEntity<?> firstTimeFreelancerForm(@RequestBody FirstTimeFreelancerFormDTO request){
        freelancerService.fillForm(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
