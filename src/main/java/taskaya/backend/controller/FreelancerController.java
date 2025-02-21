package taskaya.backend.controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import taskaya.backend.DTO.SimpleResponseDTO;
import taskaya.backend.DTO.freelancers.requests.*;
import taskaya.backend.DTO.freelancers.responses.FreelancerOwnedCommunitiesResponseDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerWorkdoneResponseDTO;
import taskaya.backend.DTO.login.FirstTimeFreelancerFormDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerSearchResponseDTO;
import taskaya.backend.DTO.freelancers.requests.FreenlancerSearchRequestDTO;
import taskaya.backend.entity.freelancer.FreelancerPortfolio;
import taskaya.backend.services.freelancer.FreelancerService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping
public class FreelancerController {

    private final FreelancerService freelancerService;

    public FreelancerController(FreelancerService freelancerService) {
        this.freelancerService = freelancerService;
    }

    @PostMapping("/freelancers/search")
    public ResponseEntity<Page<FreelancerSearchResponseDTO>> searchFreelancers(@RequestBody FreenlancerSearchRequestDTO request) {
        return ResponseEntity.ok(freelancerService.searchFreelancers(request));
    }

    @PostMapping("/freelancers/freelancer-form")
    public ResponseEntity<?> firstTimeFreelancerForm(@RequestBody FirstTimeFreelancerFormDTO request){
        freelancerService.fillForm(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/freelancers/owned-communities")
    public ResponseEntity<List<FreelancerOwnedCommunitiesResponseDTO>> freelancerOwnedCommunities(){
        return ResponseEntity.ok(freelancerService.freelancerOwnedCommunities());
    }


    @GetMapping("api/freelancers/{id}")
    public ResponseEntity<?> getProfileDetails(@PathVariable String id){
        return ResponseEntity.ok(freelancerService.getProfileDetails(id));
    }

    @PatchMapping("/freelancers/languages")
    public ResponseEntity<?> updateLanguages(@RequestBody LanguageDTO request) {
        freelancerService.updateLanguages(request);
        return ResponseEntity.ok(SimpleResponseDTO.builder()
                        .message("true")
                        .build());

    }

    @PatchMapping("/freelancers/educations")
    public ResponseEntity<?> updateEducations(@RequestBody EducationsPatchRequestDTO request) {
        freelancerService.updateEducations(request);
        return ResponseEntity.ok(SimpleResponseDTO.builder()
                .message("true")
                .build());

    }

    @PatchMapping("/freelancers/linkedin")
    public ResponseEntity<?> updateLinkedIn(@RequestBody LinkedInPatchRequestDTO request) {
        freelancerService.updateLinkedIn(request);
        return ResponseEntity.ok(SimpleResponseDTO.builder()
                .message("true")
                .build());

    }

    @PatchMapping("/freelancers/description")
    public ResponseEntity<?> updateDesc(@RequestBody DescriptionPatchRequestDTO request) {
        freelancerService.updateDesc(request);
        return ResponseEntity.ok(SimpleResponseDTO.builder()
                .message("true")
                .build());

    }

    @PatchMapping("/freelancers/employement-history")
    public ResponseEntity<?> updateEmpHistory(@RequestBody EmployeeHistoryPatchDTO request) {
        freelancerService.updateEmpHistory(request);
        return ResponseEntity.ok(SimpleResponseDTO.builder()
                .message("true")
                .build());

    }

    @PatchMapping("/freelancers/header-section")
    public ResponseEntity<?> updateHeaderSection(@RequestBody HeaderSectionUpdateRequestDTO requestDTO){
        freelancerService.updateHeaderSection(requestDTO);
        return new ResponseEntity<>(SimpleResponseDTO.builder()
                .message("header updated successfully.")
                .build(),HttpStatus.OK);
    }


    @PatchMapping("/freelancers/skills")
    public ResponseEntity<?> updateSkills(@RequestBody SkillsUpdateRequestDTO skills){
        freelancerService.updateSkills(skills);
        return new ResponseEntity<>(SimpleResponseDTO.builder()
                .message("Skills updated successfully.")
                .build(),HttpStatus.OK);
    }

    @GetMapping("api/freelancers/{id}/portfolios")
    public ResponseEntity<Page<FreelancerPortfolio>> getFreelancerPortfolios( @PathVariable String id,
                                                                              @RequestParam int page,
                                                                              @RequestParam int size) {
        // Create pageable object
        Pageable pageable = PageRequest.of(page, size);

        // Fetch paginated portfolios
        Page<FreelancerPortfolio> portfolios = freelancerService.getFreelancerPortfolios(id, pageable);

        return ResponseEntity.ok(portfolios);
    }

    @PatchMapping(value = "/freelancers/profile-picture", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProfilePicture(
            @RequestPart(value = "profilePicture") MultipartFile profilePicture) throws IOException {
        freelancerService.updateProfilePicture(profilePicture);
        return ResponseEntity.status(HttpStatus.OK).body(SimpleResponseDTO.builder().message("Profile Picture Updated!").build());
    }

    @GetMapping("api/freelancers/{id}/workdone")
    public ResponseEntity<Page<FreelancerWorkdoneResponseDTO>> freelancerWorkdone(
            @PathVariable String id, @RequestParam int page, @RequestParam int size
    ){
        return ResponseEntity.ok(freelancerService.getFreelancerWorkdone(id, page, size));
    }

    @PostMapping("/freelancers/portfolio")
    public ResponseEntity<?> addPortfolio(@RequestParam String name,
                                          @RequestPart(value = "file") MultipartFile file) throws IOException{
        freelancerService.addPortfolio(name, file);
        return ResponseEntity.status(HttpStatus.OK).body(SimpleResponseDTO.builder().message("Portfolio Added!").build());
    }

    @DeleteMapping("/freelancers/portfolio")
    public ResponseEntity<?> addPortfolio(@RequestParam String filePath)throws IOException{
        freelancerService.deletePortfolio(filePath);
        return ResponseEntity.status(HttpStatus.OK).body(SimpleResponseDTO.builder().message("Portfolio Deleted!").build());
    }

    @PatchMapping("/freelancers/avrg-hour-per-week")
    public ResponseEntity<?> updateProfilePicture(@RequestBody AvrHoursPerWeekUpdateRequestDTO requestDTO){
        freelancerService.updateAvrgHoursPerWeek(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(SimpleResponseDTO.builder().message("Avr.HoursPerWeek Updated!").build());
    }
}
