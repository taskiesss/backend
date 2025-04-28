package taskaya.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskaya.backend.DTO.clients.ClientPostedJobsResponseDTO;
import taskaya.backend.DTO.clients.ClientProfileResponseDTO;
import taskaya.backend.DTO.clients.ClientWorkDoneResponseDTO;
import taskaya.backend.DTO.workerEntity.responses.WorkerEntityWorkdoneResponseDTO;
import taskaya.backend.services.client.ClientService;

@RestController
@RequestMapping
public class ClientController {


    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/api/clients/{id}")
    public ResponseEntity<ClientProfileResponseDTO> clientProfile (
            @PathVariable String id
    ){
        return ResponseEntity.ok(clientService.getClientProfile(id));
    }

    @GetMapping("/api/clients/{id}/workdone")
    public ResponseEntity<Page<ClientWorkDoneResponseDTO>> clientWorkDone(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(clientService.getClientWorkDone(id, page, size));
    }

    @GetMapping("/clients/my-jobs")
    public ResponseEntity<Page<ClientPostedJobsResponseDTO>> clientPostedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String search
    ){
        return ResponseEntity.ok(clientService.getPostedJobs(page,size,search));
    }
}
