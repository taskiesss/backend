package taskaya.backend.controller;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import taskaya.backend.DTO.SimpleResponseDTO;
import taskaya.backend.DTO.freelancers.requests.AvrHoursPerWeekUpdateRequestDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerOwnedCommunitiesResponseDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerWorkdoneResponseDTO;
import taskaya.backend.DTO.login.FirstTimeFreelancerFormDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerSearchResponseDTO;
import taskaya.backend.DTO.freelancers.requests.FreenlancerSearchRequestDTO;
import taskaya.backend.services.freelancer.FreelancerService;

import java.io.IOException;
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

    @PatchMapping(value = "/profile-picture", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProfilePicture(
            @RequestPart(value = "profilePicture") MultipartFile profilePicture) throws IOException {
        freelancerService.updateProfilePicture(profilePicture);
        return ResponseEntity.status(HttpStatus.OK).body(SimpleResponseDTO.builder().message("Profile Picture Updated!").build());
    }

    @GetMapping("/{id}/workdone")
    public ResponseEntity<Page<FreelancerWorkdoneResponseDTO>> freelancerWorkdone(
            @PathVariable String id, @RequestParam int page, @RequestParam int size
    ){
        return ResponseEntity.ok(freelancerService.getFreelancerWorkdone(id, page, size));
    }

    @PostMapping("/portfolio")
    public ResponseEntity<?> addPortfolio(@RequestParam String name,
                                          @RequestPart(value = "file") MultipartFile file) throws IOException{
        freelancerService.addPortfolio(name, file);
        return ResponseEntity.status(HttpStatus.OK).body(SimpleResponseDTO.builder().message("Portfolio Added!").build());
    }

    @DeleteMapping("/portfolio")
    public ResponseEntity<?> addPortfolio(@RequestParam String filePath)throws IOException{
        freelancerService.deletePortfolio(filePath);
        return ResponseEntity.status(HttpStatus.OK).body(SimpleResponseDTO.builder().message("Portfolio Deleted!").build());
    }

    @PatchMapping("/avrg-hour-per-week")
    public ResponseEntity<?> updateProfilePicture(@RequestBody AvrHoursPerWeekUpdateRequestDTO requestDTO){
        freelancerService.updateAvrgHoursPerWeek(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(SimpleResponseDTO.builder().message("Avr.HoursPerWeek Updated!").build());
    }
}
