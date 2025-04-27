package taskaya.backend.controller.work;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import taskaya.backend.DTO.SimpleResponseDTO;
import taskaya.backend.DTO.milestones.responses.MilestonesDetailsResponseDTO;
import taskaya.backend.DTO.proposals.requests.SearchMyProposalsRequestDTO;
import taskaya.backend.DTO.proposals.requests.SubmitProposalRequestDTO;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.User;
import taskaya.backend.services.client.ClientService;
import taskaya.backend.services.freelancer.FreelancerService;
import taskaya.backend.services.work.ProposalService;

import java.io.DataInput;
import java.io.IOException;
import java.util.UUID;

@RestController
public class ProposalController {
    @Autowired
    private ProposalService proposalService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private FreelancerService freelancerService;
    @Autowired
    private ClientService clientService;

    @PostMapping(value = "/freelancers/proposals/{jobid}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> submitProposal(
            @PathVariable String jobid,
            @RequestPart("requestDTO") String requestDTO,
            @RequestPart(value = "attachment", required = false) MultipartFile attachment) throws MessagingException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        SubmitProposalRequestDTO dto = objectMapper.readValue(requestDTO, SubmitProposalRequestDTO.class);

        dto.setAttachment(attachment);
        proposalService.createProposal(dto, UUID.fromString(jobid));

        return ResponseEntity.status(HttpStatus.OK).body(SimpleResponseDTO.builder().message("true").build());
    }

    @GetMapping("/freelancers/my-proposals")
    public  ResponseEntity<?>getFreelancerProposals ( @RequestParam int page, @RequestParam int size){
        return ResponseEntity.ok(proposalService.getMyProposals(page,size));
    }
    @PostMapping("api/my-proposals")
    public  ResponseEntity<?>getMyProposals (@RequestBody SearchMyProposalsRequestDTO requestDTO){
        UUID workerEntityId;
        UUID ClientId;
        User user = jwtService.getUserFromToken();
        if (user.getRole() == User.Role.FREELANCER) {
            workerEntityId = freelancerService.getFreelancerFromJWT().getWorkerEntity().getId();
            ClientId = null;
        } else {
            workerEntityId = null;
            ClientId = clientService.getClientFromJWT().getId();
        }
        return ResponseEntity.ok(proposalService.searchProposals(requestDTO, workerEntityId, ClientId));
    }

    @GetMapping("/api/proposals/{id}/milestones")
    public ResponseEntity<?> getProposalMilestones (@PathVariable String id,
                                                    @RequestParam(defaultValue = "0")  int page,
                                                    @RequestParam(defaultValue = "5")  int size){
        Page<MilestonesDetailsResponseDTO> responseDTOPage = proposalService.getProposalMilestones(id,page,size);
        return  ResponseEntity.ok(responseDTOPage);
    }
}
