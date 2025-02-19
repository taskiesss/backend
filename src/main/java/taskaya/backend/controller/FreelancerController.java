package taskaya.backend.controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskaya.backend.DTO.SimpleResponseDTO;
import taskaya.backend.DTO.freelancers.requests.CountryUpdateRequestDTO;
import taskaya.backend.DTO.freelancers.requests.PricePerHourUpdateRequestDTO;
import taskaya.backend.DTO.freelancers.requests.SkillsUpdateRequestDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerOwnedCommunitiesResponseDTO;
import taskaya.backend.DTO.jobs.responses.JobSearchResponseDTO;
import taskaya.backend.DTO.login.FirstTimeFreelancerFormDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerSearchResponseDTO;
import taskaya.backend.DTO.freelancers.requests.FreenlancerSearchRequestDTO;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.freelancer.FreelancerPortfolio;
import taskaya.backend.services.freelancer.FreelancerService;

import java.util.List;

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

    @GetMapping("/owned-communities")
    public ResponseEntity<List<FreelancerOwnedCommunitiesResponseDTO>> freelancerOwnedCommunities(){
        return ResponseEntity.ok(freelancerService.freelancerOwnedCommunities());
    }

    @PatchMapping("/country")
    public ResponseEntity updateCountry(@RequestBody CountryUpdateRequestDTO country){
        freelancerService.updateCountry(country);
        return new ResponseEntity<>(SimpleResponseDTO.builder()
                .message("Country updated successfully.")
                .build(),HttpStatus.OK);
    }

    @PatchMapping("/price-per-hour")
    public ResponseEntity updatePricePerHour(@RequestBody PricePerHourUpdateRequestDTO pricePerHour){
        freelancerService.updatePricePerHour(pricePerHour);
        return new ResponseEntity<>(SimpleResponseDTO.builder()
                .message("Price per hour updated successfully.")
                .build(),HttpStatus.OK);
    }

    @PatchMapping("/skills")
    public ResponseEntity updateSkills(@RequestBody SkillsUpdateRequestDTO skills){
        freelancerService.updateSkills(skills);
        return new ResponseEntity<>(SimpleResponseDTO.builder()
                .message("Skills updated successfully.")
                .build(),HttpStatus.OK);
    }

    @GetMapping("/{id}/portfolios")
    public ResponseEntity<Page<FreelancerPortfolio>> getFreelancerPortfolios( @PathVariable String id,
                                                                              @RequestParam int page,
                                                                              @RequestParam int size){
        // Create pageable object
        Pageable pageable = PageRequest.of(page, size);

        // Fetch paginated portfolios
        Page<FreelancerPortfolio> portfolios = freelancerService.getFreelancerPortfolios(id, pageable);

        return ResponseEntity.ok(portfolios);
    }
}
