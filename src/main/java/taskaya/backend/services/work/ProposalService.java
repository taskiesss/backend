package taskaya.backend.services.work;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.milestones.requests.MilestoneSubmitProposalRequestDTO;
import taskaya.backend.DTO.proposals.requests.SubmitProposalRequestDTO;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.User;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Milestone;
import taskaya.backend.entity.work.Proposal;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.repository.work.ProposalRepository;
import taskaya.backend.services.MailService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProposalService {
    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private JobService jobService;

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


    @Transactional
    public void createProposal(SubmitProposalRequestDTO requestDTO, UUID jobId) throws MessagingException {

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


        // create milestones list, copy milestones from DTO
        List<Milestone> myMilestoneList = new ArrayList<>();
        for (MilestoneSubmitProposalRequestDTO milestone : requestDTO.getMilestones()) {
            Milestone myMilestone = Milestone.builder()
                    .name(milestone.getTitle())
                    .description(milestone.getDescription())
                    .dueDate(milestone.getDueDate())
                    .number(milestone.getMilestoneNumber())
                    .estimatedHours(milestone.getExpectedHours())
                    .status(Milestone.MilestoneStatus.NOT_STARTED)
                    .build();

            myMilestoneList.add(myMilestone);
        }

        // create proposal, copy data from DTO
        Proposal proposal = Proposal.builder()
                .job(jobService.findById(jobId))
                .workerEntity(workerEntityService.findById(UUID.fromString(requestDTO.getCandidateId())))
                .client(jobService.getClientByJobId(jobId))
                .costPerHour(requestDTO.getPricePerHour())
                .coverLetter(requestDTO.getCoverLetter())
                .payment(requestDTO.getFreelancerPayment())
                .milestones(myMilestoneList)
                .attachment(requestDTO.getAttachment())
                .build();

        proposalRepository.save(proposal);

        milestoneService.saveAll(myMilestoneList);

        String clientEmail = userRepository.findById(jobService.getClientByJobId(jobId).getId())
                .orElseThrow(()->new RuntimeException("User Not Found")).getEmail();


        String jobTitle = jobService.findById(jobId).getTitle();

        mailService.sendProposalToClient(clientEmail, workerName, jobTitle);

    }

}
