package taskaya.backend.controller;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import taskaya.backend.DTO.search.freelancers.FreelancerSearchResponseDTO;
import taskaya.backend.DTO.search.freelancers.FreenlancerSearchRequestDTO;
import taskaya.backend.services.freelancer.FreelancerService;

@RestController
@RequestMapping("/freelancers")
public class FreelancerController {

    private final FreelancerService freelancerService;

    public FreelancerController(FreelancerService freelancerService) {
        this.freelancerService = freelancerService;
    }

    @PostMapping("/search")
    public Page<FreelancerSearchResponseDTO> searchFreelancers(@RequestBody FreenlancerSearchRequestDTO request) {
        return freelancerService.searchFreelancers(request);
    }
}
