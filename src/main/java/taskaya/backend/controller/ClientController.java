package taskaya.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taskaya.backend.DTO.clients.ClientProfileResponseDTO;
import taskaya.backend.services.client.ClientService;

@RestController
@RequestMapping
public class ClientController {


    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/api/clients/{id}")
    public ResponseEntity<ClientProfileResponseDTO> getClientProfile (
            @PathVariable String id
    ){
        return ResponseEntity.ok(clientService.getClientProfile(id));
    }
}
