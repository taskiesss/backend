package taskaya.backend.services.work;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.commons.responses.MyProposalsPageResponseDTO;
import taskaya.backend.DTO.mappers.MilestonesDetailsMapper;
import taskaya.backend.DTO.mappers.MyProposalsPageResponseMapper;
import taskaya.backend.DTO.mappers.ProposalDetailsResponseMapper;
import taskaya.backend.DTO.proposals.requests.SubmitProposalRequestDTO;
import taskaya.backend.DTO.proposals.responses.ProposalDetailsResponseDTO;
import taskaya.backend.DTO.milestones.requests.MilestoneSubmitRequestMapper;
import taskaya.backend.DTO.milestones.responses.MilestonesDetailsResponseDTO;
import taskaya.backend.DTO.proposals.requests.SearchMyProposalsRequestDTO;
import taskaya.backend.DTO.proposals.responses.SearchMyProposalsResponseDTO;
import taskaya.backend.DTO.proposals.responses.SearchMyProposalsResponseMapper;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.*;
import taskaya.backend.exceptions.notFound.NotFoundException;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.repository.work.ProposalRepository;
import taskaya.backend.services.CloudinaryService;
import taskaya.backend.services.MailService;
import taskaya.backend.services.NotificationService;
import taskaya.backend.services.client.ClientService;
import taskaya.backend.services.community.CommunityService;
import taskaya.backend.services.freelancer.FreelancerService;
import taskaya.backend.specifications.ProposalSpecification;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.*;

@Service
public class ProposalService {
    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private JobService jobService;

    @Autowired
    private CommunityService communityService;
    @Autowired
    private WorkerEntityService workerEntityService;

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    FreelancerRepository freelancerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MailService mailService;

    @Autowired
    CommunityRepository communityRepository;

    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    FreelancerService freelancerService;

    @Autowired
    NotificationService notificationService;

    @Transactional
    public void createProposal(SubmitProposalRequestDTO requestDTO, UUID jobId, boolean sendEmail) throws MessagingException, IOException {

        //check that job id in path and DTO are identical
        if(!Objects.equals(jobId.toString(), requestDTO.getJobId()))
            throw new RuntimeException("Invalid Request, Job ID corrupted!");

        //get username of Freelancer or Community Admin
        String freelancerUsername;

        //get worker entity
        WorkerEntity workerEntity = workerEntityService.findById(UUID.fromString(requestDTO.getCandidateId()));

        //get worker entity type
        String workerEntityType = workerEntity.getType().name();

        // name to be send in email
        String workerName ;


        if(workerEntityType.equals("FREELANCER")){   //if freelancer
            // search for freelancer object
            Freelancer testFreelancer = freelancerRepository.findByWorkerEntity(workerEntity)
                    .orElseThrow(()->new RuntimeException("No Freelancer matches this Worker Entity!"));

            //set current username for context
            freelancerUsername = testFreelancer.getUser().getUsername();

            //set username for email
            workerName = testFreelancer.getUser().getUsername();
        }else if(workerEntityType.equals("COMMUNITY")){  //if community

            // search for community object
            Community testCommunity = communityRepository.findByWorkerEntity(workerEntity)
                    .orElseThrow(()->new RuntimeException("No Community matches this worker entity!"));

            //set username for email
            freelancerUsername = testCommunity.getAdmin().getUser().getUsername();

            //set username for email
            workerName = testCommunity.getCommunityName();
        }else{
            throw new RuntimeException("NO Worker Entity for Freelancer nor Community Found!");
        }


        //check that user with token is either a freelancer or an admin of community
        String username = JwtService.getAuthenticatedUsername();
        if(!Objects.equals(freelancerUsername, username)){
            throw new RuntimeException("Invalid Request, User authorized is different from stated in Request");
        }

        // Upload file to Cloudinary, return path URL
        String fileUrl = null;
        if (requestDTO.getAttachment() != null && !requestDTO.getAttachment().isEmpty()) {
            fileUrl = cloudinaryService.uploadFile(requestDTO.getAttachment(), "proposal_attachments");
        }


        // create milestones list, copy milestones from DTO
        List<Milestone> myMilestoneList = MilestoneSubmitRequestMapper.toMilestoneList(requestDTO.getMilestones());

        Job job = jobService.findById(jobId);
        // create proposal, copy data from DTO
        Proposal proposal = Proposal.builder()
                .job(job)
                .workerEntity(workerEntityService.findById(UUID.fromString(requestDTO.getCandidateId())))
                .client(job.getClient())
                .costPerHour(requestDTO.getPricePerHour())
                .coverLetter(requestDTO.getCoverLetter())
                .payment(requestDTO.getFreelancerPayment())
                .milestones(myMilestoneList)
                .attachment(fileUrl)
                .build();

        proposalRepository.save(proposal);

        milestoneService.saveAll(myMilestoneList);

        String clientEmail = userRepository.findById(job.getClient().getId())
                .orElseThrow(()->new RuntimeException("User Not Found")).getEmail();


        String jobTitle = jobService.findById(jobId).getTitle();

        if(sendEmail){
            notificationService.sendProposalToClient(jobTitle,proposal.getClient().getUser(),proposal.getId());
            mailService.sendProposalToClientAsync(clientEmail, workerName, jobTitle);
        }


    }

    public Page<MyProposalsPageResponseDTO> getMyProposals(int page , int size){
        Sort sort = sort = Sort.by(Sort.Order.desc("date"));
        Pageable pageable = PageRequest.of(page, size, sort);

        Freelancer freelancer = freelancerService.getFreelancerFromJWT();

        Page <Proposal> proposalsPage = proposalRepository.findByWorkerEntity(freelancer.getWorkerEntity(),pageable);

        return MyProposalsPageResponseMapper.toDTOPage(proposalsPage);
    }

    public void rejectOtherProposalsAfterStartingContract (Job job , Contract contract ) {
        Proposal proposal = proposalRepository.findByContract(contract)
                .orElseThrow(()->new RuntimeException("No Proposal found for this contract!"));
        List<Proposal> proposals = proposalRepository.findByJob(job);

        proposal.setStatus(Proposal.ProposalStatus.HIRED);

        proposals.stream().filter(proposal1 -> proposal1.getId()!= proposal.getId()
                        && proposal1.getStatus()== Proposal.ProposalStatus.PENDING)
                .forEach(proposal1 -> {
                    proposal1.setStatus(Proposal.ProposalStatus.DECLINED);
                    List<Freelancer> freelancers = workerEntityService.getFreelancersByWorkerEntity(proposal1.getWorkerEntity());
                    for(Freelancer freelancer:freelancers){
                        if(freelancer != null){
                            notificationService.sendProposalRejection(job.getTitle(),freelancer.getUser(),proposal1.getId());
                        }
                    }
                });
        proposalRepository.saveAll(proposals);

    }
    public ProposalDetailsResponseDTO getProposalDetails(String proposalId)   {
        Proposal proposal = proposalRepository.findById(UUID.fromString(proposalId))
                .orElseThrow(()-> new NotFoundException("Proposal not found!"));

        if(proposal.getWorkerEntity().getType() == WorkerEntity.WorkerType.FREELANCER){
            Freelancer freelancer = freelancerRepository.findByWorkerEntity(proposal.getWorkerEntity()).get();

            return ProposalDetailsResponseMapper.toDTO(proposal,freelancer);
        } else if (proposal.getWorkerEntity().getType() == WorkerEntity.WorkerType.COMMUNITY) {
            Community community = communityRepository.findByWorkerEntity(proposal.getWorkerEntity()).get();
            return ProposalDetailsResponseMapper.toDTO(proposal,community);
        }
        else{
            throw new IllegalArgumentException();
        }
    }

    public Page<SearchMyProposalsResponseDTO>searchProposals(SearchMyProposalsRequestDTO requestDTO,
                                                           UUID workerEntityId,UUID clientId){
        Sort sort = Sort.by(Sort.Order.desc("date"));
        Pageable pageable = PageRequest.of(requestDTO.getPage(), requestDTO.getSize(), sort);


        Specification<Proposal> specification = ProposalSpecification.searchProposal(
                requestDTO.getSearch(),
                requestDTO.getStatus(),
                workerEntityId,
                clientId,
                requestDTO.getJobId()!=null?UUID.fromString(requestDTO.getJobId()):null
        );


        Page<Proposal> proposalsPage = proposalRepository.findAll(specification, pageable);

        Page<SearchMyProposalsResponseDTO>dtoPage =  SearchMyProposalsResponseMapper.toDTOPage(proposalsPage);

        if (jwtService.getUserFromToken().getRole() == User.Role.CLIENT){
            setFreelancerNameAndFreelancerIdForProposalsDTO(dtoPage, proposalsPage);
        }
        return dtoPage;

    }




    public void setFreelancerNameAndFreelancerIdForProposalsDTO(Page<SearchMyProposalsResponseDTO> dtoPage, Page<Proposal> proposalPage) {
        for (int i = 0; i < proposalPage.getContent().size(); i++) {
            Proposal proposal = proposalPage.getContent().get(i);
            SearchMyProposalsResponseDTO dto = dtoPage.getContent().get(i);
            if (proposal.getWorkerEntity().getType() == WorkerEntity.WorkerType.COMMUNITY) {
                Community community = communityService.getCommunityByWorkerEntity(proposal.getWorkerEntity());
                dto.setFreelancerName(community.getCommunityName());
                dto.setFreelancerId(community.getUuid().toString());
                dto.setProfilePicture(community.getProfilePicture());
                dto.setFreelancerTitle(community.getTitle());
                dto.setCommunity(true);

            } else {
                Freelancer freelancer = freelancerService.getFreelancerByWorkerEntity(proposal.getWorkerEntity());
                dto.setFreelancerName(freelancer.getName());
                dto.setFreelancerId(freelancer.getId().toString());
                dto.setProfilePicture(freelancer.getProfilePicture());
                dto.setFreelancerTitle(freelancer.getTitle());
                dto.setCommunity(false);
            }
        }
    }

    public Page<MilestonesDetailsResponseDTO> getProposalMilestones(String id, int page, int size) {
        Proposal proposal = getProposalById(id);

        List<Milestone> milestones = proposal.getMilestones();
        milestones.sort(Comparator.comparing(Milestone::getDueDate));

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), milestones.size());
        List<Milestone> pagedList = milestones.subList(start, end);
        return MilestonesDetailsMapper.toPageDTO(new PageImpl<>(pagedList, pageable, milestones.size()));

    }

    public Proposal getProposalById(String proposalId){
        return proposalRepository.findById(UUID.fromString(proposalId))
                .orElseThrow(()-> new NotFoundException("No proposal Found!"));
    }


    public void hireProposalAndRejectAllOthersAfterStartingContract(Proposal proposal, Contract contract) {
        proposal.setStatus(Proposal.ProposalStatus.ACCEPTED);
        proposalRepository.save(proposal);
        rejectOtherProposalsAfterStartingContract(proposal.getJob(), contract);
    }
}
