package taskaya.backend.controller.work;


import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import taskaya.backend.DTO.SimpleResponseDTO;
import taskaya.backend.DTO.contracts.requests.MyContractsPageRequestDTO;
import taskaya.backend.DTO.contracts.responses.ContractDetailsResponseDTO;
import taskaya.backend.DTO.contracts.responses.MyContractsPageResponseDTO;
import taskaya.backend.DTO.deliverables.requests.DeliverableLinkSubmitRequestDTO;
import taskaya.backend.DTO.milestones.responses.MilestoneSubmissionResponseDTO;
import taskaya.backend.DTO.milestones.responses.MilestonesContractDetailsResponseDTO;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.enums.SortedByForContracts;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.exceptions.notFound.NotFoundException;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.services.freelancer.FreelancerService;
import taskaya.backend.services.work.ContractService;


import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
public class ContractController {
    @Autowired
    ContractService contractService;
    @Autowired
    FreelancerService freelancerService;
    @Autowired
    CommunityRepository communityRepository;

    @PostMapping("/freelancers/my-contracts")
    public ResponseEntity<?> getMyContracts (@RequestBody MyContractsPageRequestDTO requestDTO){
    Freelancer freelancer = freelancerService.getFreelancerFromJWT();
    UUID workerEntityId = freelancer.getWorkerEntity().getId();
    Page<MyContractsPageResponseDTO> result= contractService.searchContracts(requestDTO,workerEntityId,null);
    return  ResponseEntity.ok(result);
    }

    @GetMapping("/api/contracts/{id}")
    public ResponseEntity<?> getContractDetails (@PathVariable String id){
        ContractDetailsResponseDTO contractDetailsResponseDTO = contractService.getContractDetails(id);
        return  ResponseEntity.ok(contractDetailsResponseDTO);
    }

    @GetMapping("/api/contracts/{id}/milestones")
    public ResponseEntity<?> getContractMilestones (@PathVariable String id, @RequestParam int page, @RequestParam int size){
        Page<MilestonesContractDetailsResponseDTO> responseDTOPage = contractService.getContractMilestones(id,page,size);
        return  ResponseEntity.ok(responseDTOPage);
    }

    @GetMapping("/api/contracts/{contractId}/milestones/{milestoneIndex}/submission")
    public ResponseEntity<?> getMilestonesSubmission (@PathVariable String contractId, @PathVariable String milestoneIndex){
        MilestoneSubmissionResponseDTO responseDTO = contractService.getMilestoneSubmission(contractId, milestoneIndex);
        return  ResponseEntity.ok(responseDTO);
    }

    @PostMapping(value = "/api/contracts/{contractId}/milestones/{milestoneIndex}/add", consumes = {"multipart/form-data"})
    public ResponseEntity<?> addMilestoneSubmission (@PathVariable String contractId, @PathVariable String milestoneIndex,
                                                     @RequestPart(value="files", required = false) List<MultipartFile> files,
                                                     @RequestPart(value="links", required = false) String links) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        List<DeliverableLinkSubmitRequestDTO> myLinks = Collections.emptyList();
        if (links != null && !links.isEmpty()) {
            myLinks = objectMapper.readValue(links, new TypeReference<List<DeliverableLinkSubmitRequestDTO>>() {});
        }

        contractService.addMilestoneSubmission(contractId, milestoneIndex, files, myLinks);
        return  ResponseEntity.status(HttpStatus.OK).body(SimpleResponseDTO.builder().message("true").build());
    }

    @DeleteMapping("/api/contracts/{contractId}/milestones/{milestoneIndex}/{type}/{id}")
    public ResponseEntity<?> deleteSubmission(@PathVariable String contractId, @PathVariable String milestoneIndex,
                                              @PathVariable String type, @PathVariable String id) throws IOException {

        contractService.deleteSubmission(contractId, milestoneIndex, type, id);
        return  ResponseEntity.status(HttpStatus.OK).body(SimpleResponseDTO.builder().message("true").build());
    }

    @GetMapping("/freelancers/communities/{communityId}/active-contracts")
    @PreAuthorize("@jwtService.isCommunityMember(#communityId)")
    public ResponseEntity<Page<MyContractsPageResponseDTO>> activeContracts(
            @PathVariable String communityId,
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size
    ){

        Community community = communityRepository.findById(UUID.fromString(communityId))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));

        List<Contract.ContractStatus> statuses = new ArrayList<>();
        statuses.add(Contract.ContractStatus.ACTIVE);

        MyContractsPageRequestDTO myContractsPageRequestDTO = MyContractsPageRequestDTO.builder()
                .search(null)
                .contractStatus(statuses)
                .page(page)
                .size(size)
                .sortedBy(SortedByForContracts.DUE_DATE)
                .sortDirection(SortDirection.DESC)
                .build();
        return ResponseEntity.ok(contractService.searchContracts(myContractsPageRequestDTO, community.getWorkerEntity().getId(), null));
    }
}