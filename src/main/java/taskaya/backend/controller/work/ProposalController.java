package taskaya.backend.controller.work;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import taskaya.backend.DTO.proposals.requests.SubmitProposalRequestDTO;
import taskaya.backend.services.work.ProposalService;

import java.io.DataInput;
import java.io.IOException;
import java.util.UUID;

@RestController
public class ProposalController {
    @Autowired
    private ProposalService proposalService;

    @PostMapping(value = "/freelancers/proposals/{jobid}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> submitProposal(
            @PathVariable String jobid,
            @RequestPart("requestDTO") String requestDTO,
            @RequestPart(value = "attachment", required = false) MultipartFile attachment) throws MessagingException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        SubmitProposalRequestDTO dto = objectMapper.readValue(requestDTO, SubmitProposalRequestDTO.class);

        dto.setAttachment(attachment);
        proposalService.createProposal(dto, UUID.fromString(jobid));

        return ResponseEntity.status(HttpStatus.OK).body("Proposal Created!");
    }
}
