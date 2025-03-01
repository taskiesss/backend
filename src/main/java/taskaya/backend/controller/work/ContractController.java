package taskaya.backend.controller.work;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskaya.backend.DTO.contracts.requests.MyContractsPageRequestDTO;
import taskaya.backend.DTO.contracts.responses.ContractDetailsResponseDTO;
import taskaya.backend.DTO.contracts.responses.MyContractsPageResponseDTO;
import taskaya.backend.DTO.milestones.responses.MilestonesContractDetailsResponseDTO;
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

    @GetMapping("/freelancers/my-contracts/{id}")
    public ResponseEntity<?> getContractDetails (@PathVariable String id){
        ContractDetailsResponseDTO contractDetailsResponseDTO = contractService.getContractDetails(id);
        return  ResponseEntity.ok(contractDetailsResponseDTO);
    }

    @GetMapping("/freelancers/my-contracts/{id}/milestones")
    public ResponseEntity<?> getContractMilestones (@PathVariable String id, @RequestParam int page, @RequestParam int size){
        Page<MilestonesContractDetailsResponseDTO> responseDTOPage = contractService.getContractMilestones(id,page,size);
        return  ResponseEntity.ok(responseDTOPage);
    }
}
