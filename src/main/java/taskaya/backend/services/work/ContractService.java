package taskaya.backend.services.work;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import taskaya.backend.DTO.contracts.requests.MyContractsPageRequestDTO;
import taskaya.backend.DTO.contracts.responses.ContractDetailsResponseDTO;
import taskaya.backend.DTO.contracts.responses.MyContractsPageResponseDTO;
import taskaya.backend.DTO.deliverables.requests.DeliverableLinkSubmitRequestDTO;
import taskaya.backend.DTO.mappers.ContractDetailsMapper;
import taskaya.backend.DTO.mappers.MilestoneSubmissionsMapper;
import taskaya.backend.DTO.mappers.MilestonesContractDetailsMapper;
import taskaya.backend.DTO.mappers.MyContractsPageResponseMapper;
import taskaya.backend.DTO.milestones.responses.MilestoneSubmissionResponseDTO;
import taskaya.backend.DTO.milestones.responses.MilestonesContractDetailsResponseDTO;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.enums.SortedByForContracts;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.freelancer.FreelancerPortfolio;
import taskaya.backend.entity.work.*;
import taskaya.backend.exceptions.notFound.NotFoundException;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.repository.work.ContractRepository;
import taskaya.backend.repository.work.MilestoneRepository;
import taskaya.backend.services.CloudinaryService;
import taskaya.backend.services.freelancer.FreelancerService;
import taskaya.backend.specifications.ContractSpecification;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ContractService {

    @Autowired
    ContractRepository contractRepository;

    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    FreelancerRepository freelancerRepository;

    @Autowired
    CommunityRepository communityRepository;

    @Autowired
    MilestoneRepository milestoneRepository;

    public Page<MyContractsPageResponseDTO> searchContracts(MyContractsPageRequestDTO requestDTO ,
                                                              UUID workerEntityId ,UUID clientId) {

        // Create Specification based on request
        Specification<Contract> specification = ContractSpecification.searchContract(
                requestDTO.getSearch(),
                requestDTO.getContractStatus(),
                workerEntityId,
                clientId
        );

        Pageable pageable;

        if (requestDTO.getSortedBy() != null) {

            Sort sort;
            if (SortDirection.DESC.equals(requestDTO.getSortDirection())) {
                sort = Sort.by(Sort.Order.desc(requestDTO.getSortedBy().getValue().equals(SortedByForContracts.TITLE.getValue())?"job.title":requestDTO.getSortedBy().getValue()));
            } else {
                sort = Sort.by(Sort.Order.asc(requestDTO.getSortedBy().getValue().equals(SortedByForContracts.TITLE.getValue())?"job.title":requestDTO.getSortedBy().getValue()));
            }
            pageable = PageRequest.of(requestDTO.getPage(), requestDTO.getSize(), sort);
        }else {
            pageable=PageRequest.of(requestDTO.getPage(), requestDTO.getSize());
        }
        Page<Contract> contractPage = contractRepository.findAll(specification, pageable);

        return MyContractsPageResponseMapper.toDTOPage(contractPage);
    }




    public static Double getContractBudget(Contract contract){
        Double totalEstimatedHours = 0D;
        for (Milestone milestone : contract.getMilestones()){
            totalEstimatedHours+=milestone.getEstimatedHours();
        }
        return totalEstimatedHours * contract.getCostPerHour();
    }

    public static Milestone getActiveMilestone (Contract contract){
        contract.getMilestones().sort(Comparator.comparing(Milestone::getNumber));
        for (Milestone milestone : contract.getMilestones()){
            if (milestone.getStatus().equals(Milestone.MilestoneStatus.IN_PROGRESS)){
                return milestone;
            }
        }
        return null;
    }

    @PreAuthorize("@jwtService.contractDetailsAuth(#id)")
    public ContractDetailsResponseDTO getContractDetails(String id) {
        Contract contract = getContractById(id);

        String freelancerName, freelancerPicture, freelancerId;
        if(contract.getWorkerEntity().getType() == WorkerEntity.WorkerType.FREELANCER){
            Freelancer freelancer = freelancerRepository.findByWorkerEntity(contract.getWorkerEntity())
                    .orElseThrow(()-> new NotFoundException("Freelancer Not Found!"));
            freelancerName = freelancer.getName();
            freelancerPicture = freelancer.getProfilePicture();
            freelancerId = freelancer.getId().toString();
        }else {
            Community community = communityRepository.findByWorkerEntity(contract.getWorkerEntity())
                    .orElseThrow(()-> new NotFoundException("Community Not Found!"));
            freelancerName = community.getCommunityName();
            freelancerPicture = community.getProfilePicture();
            freelancerId = community.getUuid().toString();
        }

        return ContractDetailsMapper.toDTO(contract,freelancerName,freelancerPicture,freelancerId);
    }

    @PreAuthorize("@jwtService.contractDetailsAuth(#id)")
    public Page<MilestonesContractDetailsResponseDTO> getContractMilestones(String id, int page, int size){
        Contract contract = getContractById(id);

        List<Milestone> milestones = contract.getMilestones();
        milestones.sort(Comparator.comparing(Milestone::getDueDate));

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), milestones.size());
        List<Milestone> pagedList = milestones.subList(start, end);
        return MilestonesContractDetailsMapper.toPageDTO(new PageImpl<>(pagedList, pageable, milestones.size()));
    }

    @PreAuthorize("@jwtService.fileSubmissionAuth(#contractId)")
    public MilestoneSubmissionResponseDTO getMilestoneSubmission(String contractId, String milestoneIndex) {
        Contract contract = getContractById(contractId);

        Milestone milestone = contract.getMilestones().stream()
                .filter(myMilestone -> myMilestone.getNumber().toString().equals(milestoneIndex) )
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Milestone Not Found!"));

        return MilestoneSubmissionsMapper.toDTO(milestone);
    }

    @Transactional
    @PreAuthorize("@jwtService.fileSubmissionAuth(#contractId)")
    public void addMilestoneSubmission(String contractId, String milestoneIndex,
                                       List<MultipartFile> files, List<DeliverableLinkSubmitRequestDTO> links) throws IOException {

        Contract contract = getContractById(contractId);
        Milestone milestone = contract.getMilestones().stream()
                .filter(myMilestone -> myMilestone.getNumber().toString().equals(milestoneIndex) )
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Milestone Not Found!"));

        List<DeliverableFile> filesList = milestone.getDeliverableFiles();
        String fileUrl = null;
        for(MultipartFile file : files){
            if(file != null && !file.isEmpty()){
                fileUrl = cloudinaryService.uploadFile(file, "jobs_deliverables");
                filesList.add(DeliverableFile.builder()
                                .fileName(file.getOriginalFilename())
                                .filePath(fileUrl)
                                .build());
            }
        }

        List<DeliverableLink> linksList = milestone.getDeliverableLinks();
        for(DeliverableLinkSubmitRequestDTO link : links){
            if(link != null){
                linksList.add(DeliverableLink.builder()
                                .linkUrl(link.getUrl())
                                .fileName(link.getName())
                                .build());
            }
        }
        milestoneRepository.save(milestone);
    }

    private Contract getContractById(String contractId){
        return contractRepository.findById(UUID.fromString(contractId))
                .orElseThrow(()-> new NotFoundException("No Contract Found!"));
    }

    @Transactional
    @PreAuthorize("@jwtService.fileSubmissionAuth(#contractId)")
    public void deleteSubmission(String contractId, String milestoneIndex, String type, String id) throws IOException {
        Contract contract = getContractById(contractId);
        Milestone milestone = contract.getMilestones().stream()
                .filter(myMilestone -> myMilestone.getNumber().toString().equals(milestoneIndex) )
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Milestone Not Found!"));


        if(type.equals("file")){
            List<DeliverableFile> filesList = milestone.getDeliverableFiles();
            DeliverableFile file =  filesList.stream()
                    .filter(myFile -> myFile.getId().toString().equals(id))
                    .findFirst()
                    .orElseThrow(()-> new RuntimeException("File Not Found!"));

            boolean deleted = cloudinaryService.deleteFile(file.getFilePath());
            if(deleted){
                filesList.removeIf(myFile -> myFile.getId().equals(file.getId()));
                milestone.setDeliverableFiles(filesList);
                milestoneRepository.save(milestone);
            }
        }else if(type.equals("link")){
            List<DeliverableLink> linkList = milestone.getDeliverableLinks();
            DeliverableLink link = linkList.stream()
                    .filter(myLink -> myLink.getId().toString().equals(id))
                    .findFirst()
                    .orElseThrow(()-> new RuntimeException("Link Not Found!"));

                linkList.removeIf(myLink -> myLink.getId().equals(link.getId()));
                milestone.setDeliverableLinks(linkList);
                milestoneRepository.save(milestone);
        }else{
            throw new RuntimeException("File Type Missing!");
        }
    }
}
