package taskaya.backend.controller.work;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import taskaya.backend.DTO.contracts.requests.MyContractsPageRequestDTO;
import taskaya.backend.DTO.contracts.responses.MyContractsPageResponseDTO;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.services.freelancer.FreelancerService;
import taskaya.backend.services.work.ContractService;

import java.util.UUID;

@RestController
public class ContractController {
    @Autowired
    ContractService contractService;
    @Autowired
    FreelancerService freelancerService;

    @PostMapping("/freelancers/my-contracts")
    public ResponseEntity<?> getMyContracts (@RequestBody MyContractsPageRequestDTO requestDTO){
    Freelancer freelancer = freelancerService.getFreelancerFromJWT();
    UUID workerEntityId = freelancer.getWorkerEntity().getId();
    Page<MyContractsPageResponseDTO> result= contractService.searchContracts(requestDTO,workerEntityId,null);
    return  ResponseEntity.ok(result);
    }
}
