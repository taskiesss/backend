package taskaya.backend.services.work;

import jakarta.mail.MessagingException;
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
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.client.ClientBusiness;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.entity.enums.PaymentMethod;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.enums.SortedByForContracts;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.freelancer.FreelancerBusiness;
import taskaya.backend.entity.work.*;
import taskaya.backend.exceptions.notFound.NotFoundException;
import taskaya.backend.repository.client.ClientBusinessRepository;
import taskaya.backend.repository.client.ClientRepository;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.freelancer.FreelancerBusinessRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.repository.work.ContractRepository;
import taskaya.backend.repository.work.MilestoneRepository;
import taskaya.backend.services.CloudinaryService;
import taskaya.backend.services.MailService;
import taskaya.backend.services.PaymentService;
import taskaya.backend.services.community.CommunityService;
import taskaya.backend.services.freelancer.FreelancerBalanceService;
import taskaya.backend.services.freelancer.FreelancerBusinessService;
import taskaya.backend.services.freelancer.FreelancerService;
import taskaya.backend.specifications.ContractSpecification;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ContractService {

    @Autowired
    ContractRepository contractRepository;

    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    FreelancerRepository freelancerRepository;

    @Autowired
    ClientBusinessRepository clientBusinessRepository;

    @Autowired
    ClientRepository clientRepository ;

    @Autowired
    FreelancerBusinessService freelancerBusinessService;
    @Autowired
    CommunityRepository communityRepository;

    @Autowired
    MilestoneRepository milestoneRepository;

    @Autowired
    MailService mailService;

    @Autowired
    MilestoneService milestoneService;

    @Autowired
    CommunityService communityService;

    @Autowired
    JwtService jwtService;

    @Autowired
    FreelancerService freelancerService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    JobService jobService;

    @Autowired
    FreelancerBalanceService freelancerBalanceService;



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


    public static Double getMilestoneBudget(Milestone milestone , Contract contract){
        return milestone.getEstimatedHours() * contract.getCostPerHour();
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
        boolean isUserCommunityAdmin = false;
        Double memberPercentage = 0.0;

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
            isUserCommunityAdmin = setIsUserCommunityAddmin(contract,community);

            Freelancer currentFreelancer = freelancerService.getFreelancerFromJWT();
            if(contract.getStatus().equals(Contract.ContractStatus.ACTIVE) || contract.getStatus().equals(Contract.ContractStatus.ENDED))
            {
                ContractContributor contributor = contract.getContractContributors().stream()
                        .filter(cc -> cc.getFreelancer() != null && currentFreelancer.getId().equals(cc.getFreelancer().getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Contributor not found for current freelancer"));

                memberPercentage = (double) contributor.getPercentage();
            }
        }

        ContractDetailsResponseDTO responseDTO= ContractDetailsMapper.toDTO(contract,freelancerName,freelancerPicture,freelancerId,memberPercentage);

        //set the isCommunityAdmin field
        responseDTO.setIsCommunityAdmin(isUserCommunityAdmin);

        return responseDTO;
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

    @PreAuthorize("@jwtService.contractDetailsAuth(#contractId)")
    public MilestoneSubmissionResponseDTO getMilestoneSubmission(String contractId, String milestoneIndex) {
        Contract contract = getContractById(contractId);

        Milestone milestone = contract.getMilestones().stream()
                .filter(myMilestone -> myMilestone.getId().toString().equals(milestoneIndex) )
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Milestone Not Found!"));

        return MilestoneSubmissionsMapper.toDTO(milestone);
    }

    @Transactional
    @PreAuthorize("@jwtService.fileSubmissionAuth(#contractId)")
    public void addMilestoneSubmission(String contractId, String milestoneIndex,
                                       List<MultipartFile> files, List<DeliverableLinkSubmitRequestDTO> links) throws IOException {

        Contract contract = getContractById(contractId);
        if(!(contract.getStatus().equals(Contract.ContractStatus.ACTIVE))){
            throw new RuntimeException("Invalid Request, Contract no longer Active!");
        }

        Milestone milestone = contract.getMilestones().stream()
                .filter(myMilestone -> myMilestone.getId().toString().equals(milestoneIndex) )
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Milestone Not Found!"));
        if(milestone.getStatus().equals(Milestone.MilestoneStatus.APPROVED)){
            throw new RuntimeException("Cannot edit as Milestone is already Approved!");
        }

        List<DeliverableFile> filesList = milestone.getDeliverableFiles();
        String fileUrl = null;
        if(files != null) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    fileUrl = cloudinaryService.uploadFile(file, "jobs_deliverables");
                    filesList.add(DeliverableFile.builder()
                            .fileName(file.getOriginalFilename())
                            .filePath(fileUrl)
                            .build());
                }
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
        if(!(contract.getStatus().equals(Contract.ContractStatus.ACTIVE))){
            throw new RuntimeException("Invalid Request, Contract no longer Active!");
        }

        Milestone milestone = contract.getMilestones().stream()
                .filter(myMilestone -> myMilestone.getId().toString().equals(milestoneIndex) )
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Milestone Not Found!"));
        if(milestone.getStatus().equals(Milestone.MilestoneStatus.APPROVED)){
            throw new RuntimeException("Cannot edit as Milestone is already Approved!");
        }

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

    public void requestReview(String contractId, int milestoneIndex) throws MessagingException {
        Contract contract = contractRepository.findById(UUID.fromString(contractId))
                .orElseThrow(()-> new NotFoundException("Contract Not Found!"));
        Milestone milestone = milestoneService.getMilestone(contract.getMilestones(),milestoneIndex);
        if(milestone.getStatus() == Milestone.MilestoneStatus.IN_PROGRESS){
            milestone.setStatus(Milestone.MilestoneStatus.PENDING_REVIEW);
            milestoneRepository.save(milestone);


            //send to client in milestonename for jobtitle
            Client client = contract.getClient();
            WorkerEntity workerEntity = contract.getWorkerEntity();

            if(workerEntity.getType() == WorkerEntity.WorkerType.FREELANCER){
                Freelancer freelancer = freelancerRepository.findByWorkerEntity(workerEntity)
                        .orElseThrow(()-> new RuntimeException("Freelancer Not Found!"));
                mailService.sendNotificationMailToClientforReviewRequest(client.getUser().getEmail()
                        , client.getName()
                        , freelancer.getName()
                        , contract.getJob().getTitle()
                        ,getMilestoneByIndex(contract,milestoneIndex).getName());
            }
            else{
                Community community = communityRepository.findByWorkerEntity(workerEntity)
                        .orElseThrow(()-> new RuntimeException("Community Not Found!"));
                mailService.sendNotificationMailToClientforReviewRequest(client.getUser().getEmail()
                        , client.getName()
                        ,community.getCommunityName()
                        ,contract.getJob().getTitle()
                        ,getMilestoneByIndex(contract,milestoneIndex).getName());
            }


        }else{
            throw new IllegalArgumentException("Bad request - Milestone status must be IN_PROGRESS");
        }
    }




    @Transactional
    public void startContract(Contract contract ,boolean sendEmails) {

        if(contract.getWorkerEntity().getType() == WorkerEntity.WorkerType.COMMUNITY){
            Community community = communityRepository.findByWorkerEntity(contract.getWorkerEntity())
                    .orElseThrow(()-> new NotFoundException("Community Not Found!"));

            Stream<CommunityMember> assignedMembers=community.getCommunityMembers().stream().filter(communityMember -> communityMember.getFreelancer()!=null);
            double totalPercentages = assignedMembers.mapToDouble(CommunityMember::getPositionPercent).sum();
            //should do this because of orphan removal instead of creating a new list
            contract.getContractContributors().clear();
            contract.getContractContributors().addAll(
                    community.getCommunityMembers().stream()
                            .filter(communityMember -> communityMember.getFreelancer()!=null)
                            .map(communityMember ->
                                    ContractContributor.builder()
                                            .freelancer(communityMember.getFreelancer())
                                            .Percentage(communityMember.getPositionPercent()/ (float) totalPercentages)
                                            .build()
                            ).collect(Collectors.toList())
            );


        }
        contract.setStatus(Contract.ContractStatus.ACTIVE);


        contractRepository.save(contract);
        jobService.assignJobByContract(contract);
        rejectOtherContractAfterAcceptingOne(contract);
        List<Freelancer> contractFreelancers = getFreelancersFromContract(contract);

        updateFreelancerWorkInProgressFromContract(contract, contractFreelancers);
        if (sendEmails){
            for (Freelancer freelancer : contractFreelancers) {
                mailService.sendEmailForFreelancerForStartingContract(freelancer.getUser().getEmail(),
                        contract);
            }

            mailService.sendEmailForClientForStartingContract(contract.getClient().getUser().getEmail(),
                    contract);
        }
        System.out.println("contract just started : "+ contract.getId());
    }

    public void approveMilestone (String contractId, String milestoneIndex , boolean sendEmails )  {
        int milestoneIndexx = Integer.parseInt(milestoneIndex);
        Contract contract = getActiveContract(contractId);
        Milestone milestone = getMilestoneByIndex(contract, milestoneIndexx);

        if (milestone.getStatus() != Milestone.MilestoneStatus.PENDING_REVIEW)
            throw new IllegalArgumentException("Bad request - Milestone status must be PENDING_REVIEW");

        milestone.setStatus(Milestone.MilestoneStatus.APPROVED);
        List<Milestone> milestonesToBePaid = getMilestonesToBePaid(contract);

        double totalPayment = 0;


        if (milestonesToBePaid != null) {
            if (contract.getWorkerEntity().getType() == WorkerEntity.WorkerType.COMMUNITY) {
                totalPayment = paymentService.payForCommunityContract(contract, milestonesToBePaid);

            } else {
                totalPayment = paymentService.payForFreelancerContract(contract, milestonesToBePaid);

            }
            ClientBusiness clientBusiness = contract.getClient().getClientBusiness();
            clientBusiness.setTotalSpent(clientBusiness.getTotalSpent() + totalPayment);
            clientBusinessRepository.save(clientBusiness);
        }


        if (milestoneIndexx == contract.getMilestones().size()) {
            endContract(contract);
        } else {
            //set the next milestone to be in progress
            Milestone nextMilestone = getMilestoneByIndex(contract, milestoneIndexx + 1);
            nextMilestone.setStatus(Milestone.MilestoneStatus.IN_PROGRESS);
            milestoneRepository.save(nextMilestone);
        }


        if (sendEmails){
        List<Freelancer> contractFreelancers = getFreelancersFromContract(contract);
        for (Freelancer freelancer : contractFreelancers) {
            mailService.sendMailToFreelancerAfterClientApproval(freelancer.getUser().getEmail(),
                    freelancer.getUser().getUsername(), contract.getJob().getTitle(), milestone.getName());
            }
        }
        milestoneRepository.save(milestone);

    }

    public void rateContract(String contractId, int rate) {
        if (rate<1 || rate>5){
            throw new RuntimeException("Invalid Request, rate must be between 1 and 5!");
        }

        Contract contract = getContractById(contractId);
        if (contract.getStatus()!= Contract.ContractStatus.ENDED){
            throw new RuntimeException("Invalid Request, contract must be ended!");
        }
        User user = jwtService.getUserFromToken();
        if (user.getRole()== User.Role.CLIENT) {
           clientRateFreelancer(contract,rate);
        }else {
            freelancerRateClient(contract,rate);
        }

    }

    public void acceptOrRejectContract(String contractId, boolean accepted) {
        Contract contract = getContractById(contractId);
        if (contract.getStatus()!= Contract.ContractStatus.PENDING)
            throw new IllegalArgumentException("the contract must be a pending contract");

        if (accepted)
            startContract(contract,true);
        else
            rejectContract(contract,true);
    }





    //helper functions


    public static Double getContractBudget(Contract contract){
        double totalBudget = 0D;
        for (Milestone milestone : contract.getMilestones()){
            totalBudget+=getMilestoneBudget(milestone,contract);
        }
        return totalBudget;
    }

    private Boolean setIsUserCommunityAddmin(Contract contract ,Community community){

        if (contract.getWorkerEntity().getType() == WorkerEntity.WorkerType.COMMUNITY) {
            return communityService.isUserCommunityAddmin(community);
        }
        return false;
    }

    private Contract getActiveContract (String contractId){
        Contract contract = getContractById(contractId);
        if(!(contract.getStatus().equals(Contract.ContractStatus.ACTIVE))){
            throw new RuntimeException("Invalid Request, Contract no longer Active!");
        }
        return contract;
    }


    private Milestone getMilestoneByIndex(Contract contract , int index){
        return contract.getMilestones().stream()
                .filter(myMilestone -> myMilestone.getNumber() == index)
                .findFirst()
                .orElseThrow(()-> new NotFoundException("Milestone Not Found!"));
    }


    public List<Milestone> getMilestonesToBePaid(Contract contract){

        List <Milestone> milestonesToBePaid = new ArrayList<>(contract.getMilestones().stream().filter(
                milestone -> milestone.getStatus() == Milestone.MilestoneStatus.APPROVED
        ).toList());
        //sort by milestone.number
        milestonesToBePaid.sort(Comparator.comparing(Milestone::getNumber));

        if (contract.getPayment() == PaymentMethod.PerMilestones){
            return  List.of(milestonesToBePaid.getLast());
        }else if (contract.getPayment() == PaymentMethod.PerProject
                && contract.getMilestones().size() != milestonesToBePaid.size()) {
            return null;
        }else {
            return milestonesToBePaid;
        }

    }

    public List<Freelancer>getFreelancersFromContract(Contract contract){

        if(contract.getWorkerEntity().getType() == WorkerEntity.WorkerType.COMMUNITY){
            return contract.getContractContributors().stream()
                    .map(ContractContributor::getFreelancer)
                    .toList();
        }else {
            return List.of(freelancerService.getFreelancerByWorkerEntity(contract.getWorkerEntity()));
        }
    }

    public void rejectContract(Contract contract , boolean snedEmails)   {
        if (contract.getStatus()!= Contract.ContractStatus.PENDING)
            throw new IllegalArgumentException("the contract must be a pending contract");
        contract.setStatus(Contract.ContractStatus.REJECTED);
        mailService.sendRejectionMailToClient(contract.getClient().getUser().getEmail() , contract);
        contractRepository.save(contract);
    }
    public void rejectOtherContractAfterAcceptingOne(Contract acceptedContract){
        //find contracts  for this job and status is pending
        List<Contract> contracts = contractRepository
                .findAllByStatusAndJob(Contract.ContractStatus.PENDING, acceptedContract.getJob());
        for (Contract contract : contracts) {
//            if (!contract.getId().equals(acceptedContract.getId())) {   //not needed because we guarantee that the accepted contract is inProgress
                contract.setStatus(Contract.ContractStatus.REJECTED);
                contractRepository.save(contract);
//            }
        }

    }

    public void endContract(Contract contract){

        if(!(contract.getStatus().equals(Contract.ContractStatus.ACTIVE))){
            throw new RuntimeException("Invalid Request, Contract no longer Active!");
        }
        contract.setStatus(Contract.ContractStatus.ENDED);
        contract.getJob().setStatus(Job.JobStatus.DONE);
        contract.setEndDate(new Date());
        contract.getJob().setEndedAt(new Date());


        //update the client business
        ClientBusiness clientBusiness = contract.getClient().getClientBusiness();
        clientBusiness.setCompletedJobs(clientBusiness.getCompletedJobs()+1);
        clientBusinessRepository.save(clientBusiness);

        //update the freelancer or community the community business

                if(contract.getWorkerEntity().getType()== WorkerEntity.WorkerType.FREELANCER ){
                    Freelancer freelancer =freelancerService.getFreelancerByWorkerEntity(contract.getWorkerEntity());
                    freelancer.setExperienceLevel(freelancerBusinessService.incrementCompletedJobs(freelancer.getFreelancerBusiness()));

                }else {
                    Community community=communityService.getCommunityByWorkerEntity(contract.getWorkerEntity());
                    community.setExperienceLevel(freelancerBusinessService.incrementCompletedJobs(community.getFreelancerBusiness()));
                }


        contractRepository.save(contract);
    }

    @PreAuthorize("@jwtService.isClientContractOwner(#contractId) ")
    private void clientRateFreelancer(Contract contract, int rate) {
        contract.setClientRatingForFreelancer((float) rate);

        if (contract.getWorkerEntity().getType() == WorkerEntity.WorkerType.FREELANCER) {

            Freelancer freelancer = freelancerService.getFreelancerByWorkerEntity(contract.getWorkerEntity());
            freelancer.setRate(calculateRate(freelancer.getRate(),
                    freelancer.getFreelancerBusiness().getCompletedJobs(), rate));

            freelancerRepository.save(freelancer);
        } else {
            Community community = communityService.getCommunityByWorkerEntity(contract.getWorkerEntity());
            community.setRate(calculateRate(community.getRate(),
                    community.getFreelancerBusiness().getCompletedJobs(), rate));
            communityRepository.save(community);
        }

    }

    @PreAuthorize(" @jwtService.isCommunityAdminOrFreelancerForContract(#contractId)")
    private void freelancerRateClient(Contract contract, int rate) {
        if (contract.getFreelancerRatingForClient()!=null) {
            throw new RuntimeException("Client already rated!");
        }
        contract.setFreelancerRatingForClient((float) rate);
        Client client = contract.getClient();
        client.setRate(calculateRate(client.getRate(),
                client.getClientBusiness().getCompletedJobs(), rate));
        clientRepository.save(client);
    }

    private Float calculateRate(float oldRate ,int completedJobs , int newRate){
        return (oldRate * completedJobs + newRate) / (completedJobs + 1);
    }

    private void updateFreelancerWorkInProgressFromContract(Contract contract ,List<Freelancer> contractFreelancers ){
        double contractBudget = getContractBudget(contract);
        if (contract.getWorkerEntity().getType() == WorkerEntity.WorkerType.FREELANCER) {
            freelancerBalanceService.updateFreelancerworkInProgress(
                    contractFreelancers.getFirst(),
                    contractBudget
            );
        } else {
            for (ContractContributor contributor : contract.getContractContributors()) {
                freelancerBalanceService.updateFreelancerworkInProgress(
                        contributor.getFreelancer(),
                        contractBudget * contributor.getPercentage()
                );
            }
        }
    }


}



