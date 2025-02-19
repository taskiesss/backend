package taskaya.backend.controller;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskaya.backend.DTO.SimpleResponseDTO;
import taskaya.backend.DTO.freelancers.requests.*;
import taskaya.backend.DTO.freelancers.responses.FreelancerOwnedCommunitiesResponseDTO;
import taskaya.backend.DTO.login.FirstTimeFreelancerFormDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerSearchResponseDTO;
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileDetails(@PathVariable String id){
        return ResponseEntity.ok(freelancerService.getProfileDetails(id));
    }

    @PatchMapping("/languages")
    public ResponseEntity<?> updateLanguages(@RequestBody LanguageDTO request) {
        freelancerService.updateLanguages(request);
        return ResponseEntity.ok(SimpleResponseDTO.builder()
                        .message("true")
                        .build());

    }

    @PatchMapping("/educations")
    public ResponseEntity<?> updateEducations(@RequestBody EducationsPatchRequestDTO request) {
        freelancerService.updateEducations(request);
        return ResponseEntity.ok(SimpleResponseDTO.builder()
                .message("true")
                .build());

    }

    @PatchMapping("/linkedin")
    public ResponseEntity<?> updateLinkedIn(@RequestBody LinkedInPatchRequestDTO request) {
        freelancerService.updateLinkedIn(request);
        return ResponseEntity.ok(SimpleResponseDTO.builder()
                .message("true")
                .build());

    }

    @PatchMapping("/description")
    public ResponseEntity<?> updateDesc(@RequestBody DescriptionPatchRequestDTO request) {
        freelancerService.updateDesc(request);
        return ResponseEntity.ok(SimpleResponseDTO.builder()
                .message("true")
                .build());

    }

    @PatchMapping("/employement-history")
    public ResponseEntity<?> updateEmpHistory(@RequestBody EmployeeHistoryPatchDTO request) {
        freelancerService.updateEmpHistory(request);
        return ResponseEntity.ok(SimpleResponseDTO.builder()
                .message("true")
                .build());

    }
}
