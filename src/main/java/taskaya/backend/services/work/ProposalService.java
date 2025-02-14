package taskaya.backend.services.work;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.milestones.requests.MilestoneSubmitProposalRequestDTO;
import taskaya.backend.DTO.proposals.requests.SubmitProposalRequestDTO;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.User;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Milestone;
import taskaya.backend.entity.work.Proposal;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.repository.work.ProposalRepository;
import taskaya.backend.services.MailService;

import java.util.ArrayList;
import java.util.List;
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

        Boolean freelancerORcommunity;
        if(!(freelancerRepository.findByWorkerEntity(workerEntityService.findById(UUID.fromString(requestDTO.getCandidateId()))).isEmpty())){
            freelancerORcommunity = true; //if it is freelancer
        }else if (!(communityRepository.findByWorkerEntity(workerEntityService.findById(UUID.fromString(requestDTO.getCandidateId()))).isEmpty())){
            freelancerORcommunity = false; //if it is community
        }else{
            throw new RuntimeException("WorkerEntity Not Found!");
        }



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


        String workerUsername ;
        if(freelancerORcommunity){
            // if it is Freelancer
            workerUsername = freelancerRepository
                .findByWorkerEntity(workerEntityService.findById(UUID.fromString(requestDTO.getCandidateId())))
                .orElseThrow(()->new RuntimeException("Freelancer Not Found")).getUser().getUsername();
        }
        else{
            //if it is Community
            workerUsername = communityRepository
                    .findByWorkerEntity(workerEntityService.findById(UUID.fromString(requestDTO.getCandidateId())))
                    .orElseThrow(()->new RuntimeException("Freelancer Not Found")).getCommunityName();

            System.out.println(workerUsername);
        }

        String jobTitle = jobService.findById(jobId).getTitle();

        mailService.sendProposalToClient(clientEmail, workerUsername, jobTitle);

    }

}
