package taskaya.backend.controller.work;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskaya.backend.DTO.proposals.requests.SubmitProposalRequestDTO;
import taskaya.backend.services.work.ProposalService;

import java.util.UUID;

@RestController
public class ProposalController {
    @Autowired
    private ProposalService proposalService;

    @PostMapping("/freelancers/proposals/{jobid}")
    public ResponseEntity<?> submitProposal(@PathVariable String jobid, @RequestBody SubmitProposalRequestDTO requestDTO) throws MessagingException {
        proposalService.createProposal(requestDTO, UUID.fromString(jobid));
        return ResponseEntity.status(201).body("{\"type\": \"successful\", \"message\": \"Job application submitted successfully.\"}");
    }
}
