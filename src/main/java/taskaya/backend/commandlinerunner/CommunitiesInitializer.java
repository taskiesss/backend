package taskaya.backend.commandlinerunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import taskaya.backend.entity.Payment;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.enums.PaymentMethod;
import taskaya.backend.entity.enums.ProjectLength;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.*;
import taskaya.backend.repository.PaymentRepository;
import taskaya.backend.repository.SkillRepository;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.client.ClientRepository;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.repository.work.ContractRepository;
import taskaya.backend.repository.work.JobRepository;
import taskaya.backend.repository.work.ProposalRepository;
import taskaya.backend.services.community.CommunityMemberService;
import taskaya.backend.services.community.CommunityService;

import java.util.*;

@Component
public class CommunitiesInitializer {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FreelancerRepository freelancerRepository;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private CommunityService communityService;
    @Autowired
    private CommunityMemberService communityMemberService;
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private ContractRepository contractRepository;


    public void communityWithAdmin(){
        User user = userRepository.findByUsername("freelancer01").get();
        Freelancer freelancer =freelancerRepository.findByUser(user).get();
        //userRepository.save(user);
        List<String> mySkills = List.of("Java", "Spring Boot", "Spring Security", "Spring Data JPA", "Hibernate");
        List<Skill> skills = skillRepository.findByNameIn(mySkills);

        WorkerEntity workerEntity = WorkerEntity.builder()
                .type(WorkerEntity.WorkerType.COMMUNITY)
                .build();
        Community community = Community.builder()
                .communityName("Pablo Community")
                .admin(freelancer)
                .workerEntity(workerEntity)
                .avrgHoursPerWeek(6)
                .pricePerHour(35)
                .status(Community.CommunityStatus.AVAILABLE)
                .rate(3)
                .skills(new HashSet<>(skills))
                .description("Welcome to PABLO community , where innovation meets excellence.\n" +
                        "\n" +
                        "We are a passionate team of software developers dedicated to delivering cutting-edge solutions tailored to your business needs. With expertise in web, mobile, and cloud technologies, we transform ideas into fully functional, user-friendly products.\n" +
                        "\n" +
                        "Our services include:\n" +
                        "✅ Custom Web Development\n" +
                        "✅ Mobile App Development (iOS & Android)\n" +
                        "✅ Cloud Integration & Hosting Solutions\n" +
                        "✅ Database Design & Optimization\n" +
                        "✅ API Development & Third-party Integrations\n" +
                        "✅ E-commerce Platforms & Payment Systems\n" +
                        "✅ Tailor-made Software Solutions")
                .experienceLevel(ExperienceLevel.expert)
                .build();

//		communityService.save(community);
//		community.getCommunityMembers().add(CommunityMember.builder().community(community).freelancer(freelancerRepository.findByUser(user).get()).positionName("fullstack").build());
        CommunityMember communityMember = CommunityMember.builder()
                .community(community)
                .freelancer(freelancer)
                .positionName("backend01")
                .positionPercent(40)
                .build();


        CommunityMember communityMember2 = CommunityMember.builder()
                .community(community)
                .positionPercent(30)
                .freelancer(freelancerRepository.findByUser(userRepository.findByUsername("freelancer02").get()).get())
                .positionName("backend02")
                .build();

        CommunityMember communityMember3 = CommunityMember.builder()
                .community(community)
                .positionPercent(30)
                .freelancer(freelancerRepository.findByUser(userRepository.findByUsername("freelancer03").get()).get())
                .positionName("backend03")
                .build();
        community.getCommunityMembers().addAll(List.of(communityMember,communityMember2,communityMember3));
        communityService.save(community);

    }





    public void communityWorkdoneSeed() {
        Community community = communityRepository.findByCommunityName("Pablo Community").orElseThrow();
        Client client = clientRepository.findByUser(userRepository.findByUsername("client01").orElseThrow()).orElseThrow();
        System.out.println("pablo Community UUID: "+community.getUuid());

        Job job = Job.builder()
                .title("researchers team")
                .client(client)
                .experienceLevel(ExperienceLevel.intermediate)
                .projectLength(ProjectLength._3_to_6_months)
                .status(Job.JobStatus.DONE)
                .description("We are looking for a dedicated team of researchers specializing in microwave signal technologies to work on cutting-edge advancements in this field. The team will focus on designing, analyzing, and optimizing microwave signal systems for various applications, including wireless communication, radar systems, and signal processing.\n" +
                        "\n" +
                        "Responsibilities:\n" +
                        "\t•\tConduct research on microwave signal transmission, propagation, and interference mitigation.\n" +
                        "\t•\tDevelop innovative techniques for improving signal efficiency, bandwidth utilization, and noise reduction.\n" +
                        "\t•\tDesign and optimize microwave circuits, antennas, and waveguides.\n" +
                        "\t•\tPerform simulations and testing using industry-standard tools.\n" +
                        "\t•\tCollaborate with engineers and scientists to integrate microwave technologies into practical applications.\n" +
                        "\t•\tPublish research findings in technical journals and conferences.\n")
                .pricePerHour(40)
                .endedAt(new Date(2024-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
                .assignedTo(community.getWorkerEntity())
                .build();


        List<Milestone> milestones = List.of(
                Milestone.builder()
                        .name("mile1")
                        .description("first desc")
//						.deliverableLinks(linksList)
//						.deliverableFiles(filesList)
                        .number(1)
                        .estimatedHours(120)
                        .dueDate( new Date(2026-1900, 1, 20, 15, 30, 0))
                        .status(Milestone.MilestoneStatus.APPROVED)
                        .build(),

                Milestone.builder()
                        .name("mile2")
                        .description("sec desc")
                        .number(2)
                        .dueDate(new Date(2027-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
                        .estimatedHours(300)
                        .status(Milestone.MilestoneStatus.APPROVED)
                        .build()
        );

        Contract contract = Contract.builder()
                .job(job)
                .client(client)
                .status(Contract.ContractStatus.ACTIVE)
                .milestones(milestones)
                .payment(PaymentMethod.PerProject)
                .workerEntity(community.getWorkerEntity())
                .hoursWorked(420)
                .costPerHour(50D)
                .endDate(new Date())
                .build();
        job.setContract(contract);

        Proposal proposal1= Proposal.builder()
                .costPerHour(30D)
                .date(new Date())
                .milestones(milestones)
                .contract(contract)
                .client(client)
                .status(Proposal.ProposalStatus.HIRED)
                .job(job)
                .payment(PaymentMethod.PerProject)
                .workerEntity(community.getWorkerEntity())
                .coverLetter("please accept me")
                .build();
        Double paymentAmount = 420 * 50.0;

        Payment clientPayment =Payment.builder()
                .sender(client.getUser())
                .amount(paymentAmount)
                .community(community)
                .contract(contract)
                .type(Payment.Type.TRANSACTION)
                .build();

        //create payments for each community memeber for this job
        List <Payment> paymentsForCommunityMembers = new LinkedList<>();
        for (
                CommunityMember communityMember :
                communityMemberService.getAssignedMembers(community.getCommunityMembers())
        ) {
            paymentsForCommunityMembers.add(
                    Payment.builder()
                            .sender(client.getUser())
                            .amount(communityMember.getPositionPercent() * paymentAmount/100)
                            .community(community)
                            .contract(contract)
                            .receiver(communityMember.getFreelancer().getUser())
                            .type(Payment.Type.TRANSACTION)
                            .build()
            );
        }
        paymentRepository.save(clientPayment);
        paymentRepository.saveAll(paymentsForCommunityMembers);
        jobRepository.save(job);
        proposalRepository.save(proposal1);
        communityRepository.save(community);
        contractRepository.save(contract);

//		System.out.println("Community Contract 1 ID: "+contract.getId());
//		System.out.println("Community Contract 1, milestone 1 ID: "+contract.getMilestones().get(0).getId());
//		System.out.println("Community Contract 1, milestone 2 ID: "+contract.getMilestones().get(1).getId());

        Job job2 = Job.builder()
                .title("Full-Stack Development Team")
                .client(client)
                .experienceLevel(ExperienceLevel.intermediate)
                .projectLength(ProjectLength._3_to_6_months)
                .status(Job.JobStatus.DONE)
                .description("web and software applications. The team will focus on researching best practices, emerging technologies, and optimization techniques to enhance both frontend and backend development.\n" +
                        "\n" +
                        "Responsibilities:\n" +
                        "\t•\tConduct research on the latest frontend and backend frameworks, tools, and architectures.\n" +
                        "\t•\tAnalyze and optimize performance, scalability, and security of full-stack applications.\n" +
                        "\t•\tDevelop proof-of-concept (PoC) projects to test new technologies and methodologies.\n" +
                        "\t•\tInvestigate microservices, serverless architectures, and cloud computing trends.\n" +
                        "\t•\tExplore AI-driven automation in full-stack development.\n" +
                        "\t•\tDocument research findings and provide recommendations for implementation.\n" +
                        "\t•\tCollaborate with developers, engineers, and designers to integrate research outcomes into real-world applications."
                )
                .pricePerHour(40)
                .endedAt(new Date(2025-1900, 1, 20, 15, 30, 0))
                .assignedTo(community.getWorkerEntity())
                .build();


        List<Milestone> milestones2 = List.of(
                Milestone.builder()
                        .name("mile1")
                        .number(1)
                        .estimatedHours(400)
                        .dueDate(new Date(2025-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
                        .status(Milestone.MilestoneStatus.APPROVED)
                        .build(),

                Milestone.builder()
                        .name("mile2")
                        .number(2)
                        .estimatedHours(300)
                        .dueDate(new Date(2027-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
                        .status(Milestone.MilestoneStatus.APPROVED)
                        .build()
        );

        Contract contract2 = Contract.builder()
                .job(job2)
                .client(client)
                .status(Contract.ContractStatus.ENDED)
                .milestones(milestones2)
                .workerEntity(community.getWorkerEntity())
                .payment(PaymentMethod.PerProject)
                .hoursWorked(700)
                .costPerHour(10D)
                .build();
        job2.setContract(contract2);

        Proposal proposal2= Proposal.builder()
                .costPerHour(30D)
                .date(new Date())
                .milestones(milestones2)
                .contract(contract2)
                .client(client)
                .status(Proposal.ProposalStatus.HIRED)
                .job(job2)
                .payment(PaymentMethod.PerProject)
                .workerEntity(community.getWorkerEntity())
                .coverLetter("please accept me")
                .build();

        paymentAmount = 700*10.0;
        Payment clientPayment2 =Payment.builder()
                .sender(client.getUser())
                .amount(paymentAmount)
                .community(community)
                .contract(contract2)
                .type(Payment.Type.TRANSACTION)
                .build();

        //create payments for each community memeber for this job
        List <Payment> paymentsForCommunityMembers2 = new LinkedList<>();
        for (
                CommunityMember communityMember :
                communityMemberService.getAssignedMembers(community.getCommunityMembers())
        ) {
            paymentsForCommunityMembers2.add(
                    Payment.builder()
                            .sender(client.getUser())
                            .amount(communityMember.getPositionPercent() * paymentAmount/100)
                            .community(community)
                            .contract(contract2)
                            .receiver(communityMember.getFreelancer().getUser())
                            .type(Payment.Type.TRANSACTION)
                            .build()
            );
        }
        paymentRepository.save(clientPayment2);
        paymentRepository.saveAll(paymentsForCommunityMembers2);
        communityRepository.save(community);
        jobRepository.save(job2);
        proposalRepository.save(proposal2);
        contractRepository.save(contract2);

    }


}
