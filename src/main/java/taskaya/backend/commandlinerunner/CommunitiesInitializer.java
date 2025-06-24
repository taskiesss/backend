package taskaya.backend.commandlinerunner;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import taskaya.backend.config.Constants;
import taskaya.backend.entity.Payment;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.entity.community.JoinRequest;
import taskaya.backend.entity.community.Vote;
import taskaya.backend.entity.community.posts.Post;
import taskaya.backend.entity.community.posts.PostComment;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.enums.PaymentMethod;
import taskaya.backend.entity.enums.ProjectLength;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.freelancer.FreelancerBalance;
import taskaya.backend.entity.freelancer.FreelancerBusiness;
import taskaya.backend.entity.work.*;
import taskaya.backend.repository.PaymentRepository;
import taskaya.backend.repository.SkillRepository;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.client.ClientRepository;
import taskaya.backend.repository.community.*;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.repository.work.ContractRepository;
import taskaya.backend.repository.work.JobRepository;
import taskaya.backend.repository.work.ProposalRepository;
import taskaya.backend.services.community.CommunityMemberService;
import taskaya.backend.services.community.CommunityPostService;
import taskaya.backend.services.community.CommunityService;
import taskaya.backend.services.work.ContractService;

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

    @Autowired
    private ContractService contractService;

    @Autowired
    private CommunityJoinRequestRepository communityJoinRequestRepository;

    @Autowired
    private CommunityVoteRepository communityVoteRepository;

    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Autowired
    private CommunityPostCommentRepository communityPostCommentRepository;

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
                .pricePerHour(35)
                .country("cairo,Egypt")
                .title("software development")
                .status(Community.CommunityStatus.AVAILABLE)
                .rate(4.5F)
                .profilePicture(Constants.PABLO_COMMUNITY_PROFILE_PICTURE)
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
        community.getFreelancerBusiness().setAvgHoursPerWeek(300);

//		communityService.save(community);
//		community.getCommunityMembers().add(CommunityMember.builder().community(community).freelancer(freelancerRepository.findByUser(user).get()).positionName("fullstack").build());
        CommunityMember communityMember = CommunityMember.builder()
                .community(community)
                .freelancer(freelancer)
                .positionName("backend01")
                .positionPercent(20)
                .description("Backend design and implementation")
                .build();


        CommunityMember communityMember2 = CommunityMember.builder()
                .community(community)
                .positionPercent(20)
                .freelancer(freelancerRepository.findByUser(userRepository.findByUsername("freelancer02").get()).get())
                .positionName("backend02")
                .description("Backend design and implementation")
                .build();

        CommunityMember communityMember3 = CommunityMember.builder()
                .community(community)
                .positionPercent(20)
                .freelancer(freelancerRepository.findByUser(userRepository.findByUsername("freelancer03").get()).get())
                .positionName("backend03")
                .description("Backend design and implementation")
                .build();
        community.getCommunityMembers().addAll(List.of(communityMember,communityMember2,communityMember3));
        communityService.save(community);

    }


    public void communityWorkdoneSeed() throws MessagingException {
        Community community = communityRepository.findByCommunityName("Pablo Community").orElseThrow();
        Client client = clientRepository.findByUser(userRepository.findByUsername("client01").orElseThrow()).orElseThrow();
        System.out.println("pablo Community UUID: "+community.getUuid());

        List<String> skillNames1 = List.of("Java", "Spring Boot", "Spring Security", "Spring Data JPA", "Hibernate");
        List<Skill> skills1 = skillRepository.findByNameIn(skillNames1);

        Job job = Job.builder()
                .title("researchers team")
                .client(client)
                .experienceLevel(ExperienceLevel.intermediate)
                .projectLength(ProjectLength._3_to_6_months)
                .skills(new HashSet<>(skills1))
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


        ArrayList<Milestone> milestones = new ArrayList<>(List.of(
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
                        .status(Milestone.MilestoneStatus.PENDING_REVIEW)
                        .build()
        ));

        Contract contract = Contract.builder()
                .job(job)
                .client(client)
                .status(Contract.ContractStatus.ENDED)
                .milestones(milestones)
                .payment(PaymentMethod.PerProject)
                .workerEntity(community.getWorkerEntity())
                .clientRatingForFreelancer(4F)
                .costPerHour(20D)
                .endDate(new Date())
                .build();
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
        jobRepository.save(job);
        proposalRepository.save(proposal1);
        contractRepository.save(contract);
        contractService.startContract(contract,false);

        milestones.getFirst().setStatus(Milestone.MilestoneStatus.APPROVED);
        job.setContract(contract);
//        contractService.endContract(contract);
        contractService.approveMilestone(contract.getId().toString(),"2",false);
        jobRepository.save(job);


        Double paymentAmount = 420 * 50.0;

        //create payments for each community memeber for this job

        jobRepository.save(job);
        communityRepository.save(community);
        contractRepository.save(contract);

//		System.out.println("Community Contract 1 ID: "+contract.getId());
//		System.out.println("Community Contract 1, milestone 1 ID: "+contract.getMilestones().get(0).getId());
//		System.out.println("Community Contract 1, milestone 2 ID: "+contract.getMilestones().get(1).getId());

        Job job2 = Job.builder()
                .title("Full-Stack Development Team")
                .client(client)
                .skills(new HashSet<>(skills1))
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


        ArrayList<Milestone> milestones2 = new ArrayList<>(List.of(
                Milestone.builder()
                        .name("mile1")
                        .number(1)
                        .description("the primary goal is to establish a strong base for the full-stack application. This includes selecting and configuring the core technologies, setting up the development environment, and laying out the fundamental project structure on both the frontend and backend sides. Key activities and deliverables often include:\n" +
                                "\n" +
                                "Technical Stack Selection: Finalize the chosen frameworks, libraries, and tools (e.g., React for the frontend, Node.js/Express for the backend, etc.) based on project requirements and team expertise.\n" +
                                "\n" +
                                "Repository and Version Control Setup: Create the Git repository (or use another version control system) to ensure collaboration and code management best practices are in place.\n" +
                                "\n" +
                                "Project Architecture & Folder Structure: Define a clear, standardized architecture for frontend and backend modules (e.g., naming conventions, file organization, key directory layout) to maintain consistency and scalability.\n" +
                                "\n" +
                                "Initial Build Scripts & Basic Configuration: Configure build tools (e.g., Webpack) or other bundlers, set up test scripts, and prepare basic deployment scripts if applicable.\n" +
                                "\n")
                        .estimatedHours(400)
                        .dueDate(new Date(2025-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
                        .status(Milestone.MilestoneStatus.APPROVED)
                        .build(),

                Milestone.builder()
                        .name("mile2")
                        .number(2)
                        .description("the focus shifts toward establishing the data layer and implementing essential user-related functionality. The goal is to ensure that the application’s core entities are properly stored and retrieved, and that users can securely access and interact with the system. Key objectives typically include:\n" +
                                "\n" +
                                "Database Setup & Schema Design\n" +
                                "\n" +
                                "Select and configure the database solution (e.g., PostgreSQL, MongoDB).\n" +
                                "\n" +
                                "Define the initial data models and relationships needed to support core features.\n" +
                                "\n" +
                                "Implement database migrations or seeding strategies (if required) to keep the schema consistent across environments.\n" +
                                "\n" +
                                "User Authentication & Authorization\n" +
                                "\n" +
                                "Implement a secure authentication flow (e.g., JWT-based tokens or session-based auth).\n" +
                                "\n" +
                                "Set up basic user registration, login, and logout functionality.\n" +
                                "\n" +
                                "Integrate role-based or permission-based access controls as applicable.\n" +
                                "\n" +
                                "Backend API Endpoints\n" +
                                "\n" +
                                "Create foundational CRUD endpoints for key data entities (e.g., users, products, or other relevant resources).\n" +
                                "\n" +
                                "Enforce authentication and authorization on protected routes to prevent unauthorized access.\n" +
                                "\n" +
                                "Frontend Integration\n" +
                                "\n" +
                                "Develop forms or UI components to register new users and log in existing ones.\n" +
                                "\n" +
                                "Ensure frontend authentication flows are correctly integrated with the backend (e.g., storing tokens, handling session).\n" +
                                "\n" +
                                "Present user-specific data in a basic dashboard or relevant views, confirming the end-to-end data flow.")
                        .estimatedHours(300)
                        .dueDate(new Date(2027-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
                        .status(Milestone.MilestoneStatus.PENDING_REVIEW)
                        .build()
        ));

        Contract contract2 = Contract.builder()
                .job(job2)
                .client(client)
                .status(Contract.ContractStatus.ENDED)
                .milestones(milestones2)
                .workerEntity(community.getWorkerEntity())
                .payment(PaymentMethod.PerProject)
                .clientRatingForFreelancer(5F)
                .costPerHour(10D)
                .build();
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
        jobRepository.save(job2);
        proposalRepository.save(proposal2);
        System.out.println("community hired proposal : "+proposal2.getId());
        contractRepository.save(contract2);
        contractService.startContract(contract2,false);
        milestones2.getFirst().setStatus(Milestone.MilestoneStatus.APPROVED);
        job2.setContract(contract2);
        contractService.approveMilestone(contract2.getId().toString(),"2",false);
        jobRepository.save(job2);


        communityRepository.save(community);
        jobRepository.save(job2);
        contractRepository.save(contract2);

    }


    public void communityWithContractPendingAndVotes() {
        Community pabloCommunity = communityRepository.findByCommunityName("Pablo Community").get();
        Milestone milestone1 = Milestone.builder()
                .name("\uD83C\uDFC1 Milestone 1:")
                .number(1)
                .description("-Understand the business goals and user needs \uD83D\uDCA1\n" +
                        "\n" +
                        "-Define core features (sign-up, workout plans, bookings, etc.) \uD83D\uDCDD\n" +
                        "\n" +
                        "-Create wireframes and interactive UI/UX prototypes \uD83C\uDFA8\n" +
                        "\n" +
                        "-Finalize app flow and get design approval ✅")
                .status(Milestone.MilestoneStatus.NOT_STARTED)
                .dueDate(new Date())
                .estimatedHours(45)
                .build();

        Milestone milestone2 = Milestone.builder()
                .name("\uD83D\uDEE0\uFE0F Milestone 2")
                .number(2)
                .description("-Set up server, database, and API structure ⚙\uFE0F\n" +
                        "\n" +
                        "-Develop user authentication & role management \uD83D\uDD10\n" +
                        "\n" +
                        "-Design database schema (users, classes, trainers, bookings) \uD83D\uDDC3\uFE0F\n" +
                        "\n" +
                        "-Implement basic admin dashboard \uD83D\uDCCA")
                .status(Milestone.MilestoneStatus.NOT_STARTED)
                .dueDate(new Date())
                .estimatedHours(45)
                .build();

        Milestone milestone3 = Milestone.builder()
                .name("\uD83D\uDCF1 Milestone 3")
                .number(3)
                .description("-Build responsive mobile or web UI (React Native / Flutter / React) \uD83D\uDCF2\n" +
                        "\n" +
                        "-Integrate workouts, schedule, and trainer profiles \uD83C\uDFCB\uFE0F\u200D♀\uFE0F\n" +
                        "\n" +
                        "-Enable real-time bookings and notifications \uD83D\uDCC6\uD83D\uDD14\n" +
                        "\n" +
                        "-Connect frontend to backend with secure APIs \uD83D\uDD04\n" +
                        "\n")
                .status(Milestone.MilestoneStatus.NOT_STARTED)
                .dueDate(new Date())
                .estimatedHours(45)
                .build();

        Milestone milestone4 = Milestone.builder()
                .name("\uD83D\uDE80 Milestone 4:")
                .number(4)
                .description("Perform QA testing & bug fixing \uD83D\uDC1E\uD83D\uDEE0\uFE0F\n" +
                        "\n" +
                        "Optimize performance and responsiveness \uD83D\uDEA6\n" +
                        "\n" +
                        "Deploy to app stores or web hosting platforms \uD83C\uDF10\uD83D\uDCE4\n" +
                        "\n" +
                        "Provide documentation and post-launch support \uD83D\uDCDA\uD83E\uDD1D")
                .status(Milestone.MilestoneStatus.NOT_STARTED)
                .dueDate(new Date())
                .estimatedHours(45)
                .build();

        ArrayList<Milestone> listmilestone = new ArrayList<>();

        listmilestone.addAll(List.of(milestone1,milestone2,milestone3));


        Contract pendingContract = Contract.builder()
                .client(clientRepository.findByUser(userRepository.findByUsername("client01").get()).get())
                .job(jobRepository.findByTitle("GYM mobile application").get())
                .workerEntity(pabloCommunity.getWorkerEntity())
                .costPerHour(45.77)
                .status(Contract.ContractStatus.PENDING)
                .payment(PaymentMethod.PerProject)
                .milestones(listmilestone)
                .build();

        Proposal proposal = Proposal.builder()
                .costPerHour(45.77)
                .date(new Date())
                .milestones(listmilestone)
                .contract(pendingContract)
                .client(clientRepository.findByUser(userRepository.findByUsername("client01").get()).get())
                .status(Proposal.ProposalStatus.HIRED)
                .job(jobRepository.findByTitle("GYM mobile application").get())
                .payment(PaymentMethod.PerProject)
                .workerEntity(pabloCommunity.getWorkerEntity())
                .coverLetter("please accept me")
                .build();
        proposalRepository.save(proposal);

        CommunityMember person1 = pabloCommunity.getCommunityMembers().get(0);
        System.out.println("person1 :" + person1.getFreelancer().getName());
        CommunityMember person2 = pabloCommunity.getCommunityMembers().get(1);
        System.out.println("person2 :" + person2.getFreelancer().getName());
        CommunityMember person3 = pabloCommunity.getCommunityMembers().get(2);
        System.out.println("person3 :" + person3.getFreelancer().getName());

        contractRepository.save(pendingContract);
        System.out.println("Contact id:" + pendingContract.getId());


        Vote yesVote = Vote.builder()
                .contract(pendingContract)
                .communityMember(person1)
                .agreed(null)
                .build();

        Vote noVote = Vote.builder()
                .contract(pendingContract)
                .communityMember(person2)
                .agreed(true)
                .build();

        Vote nullVote = Vote.builder()
                .contract(pendingContract)
                .communityMember(person3)
                .agreed(true)
                .build();


        communityVoteRepository.save(yesVote);
        communityVoteRepository.save(noVote);
        communityVoteRepository.save(nullVote);

    }

    public void communityWithActiveJob(){
        Community pabloCommunity = communityRepository.findByCommunityName("Pablo Community").get();

        Job activeJob = jobRepository.findByTitle("club website").get();
        activeJob.setStatus(Job.JobStatus.IN_PROGRESS);
        activeJob.setAssignedTo(pabloCommunity.getWorkerEntity());
        jobRepository.save(activeJob);

        //creating milestones for active contract and the first milestone status will be in progress
        //milestone 1
        Milestone milestone1 = Milestone.builder()
                .name("milestone 1")
                .number(1)
                .status(Milestone.MilestoneStatus.NOT_STARTED)
                .dueDate(new Date())
                .description("\uD83D\uDE80 The first milestone of our club website development is officially complete! " +
                        "\uD83E\uDD73 We kicked things off by laying down a strong foundation \uD83E\uDDF1—starting with" +
                        " a fully responsive homepage \uD83D\uDDA5\uFE0F\uD83D\uDCF1 that captures the club’s identity " +
                        "\uD83C\uDFAF. We implemented smooth navigation \uD83D\uDD17, a sleek UI design ✨, and an" +
                        " engaging landing section that tells our story \uD83D\uDCD6. We also integrated a basic member " +
                        "login system \uD83D\uDD10 to prepare for future personalized features. The homepage is now " +
                        "live with sections like About Us \uD83D\uDC65, Events \uD83D\uDCC5, and Contact ✉\uFE0F—each built " +
                        "with accessibility and performance in mind ⚙\uFE0F. Behind the scenes, we set up our codebase on" +
                        " GitHub \uD83E\uDDD1\u200D\uD83D\uDCBB\uD83D\uDCC1, organized our components for scalability " +
                        "\uD83D\uDCD0, and documented everything for future developers \uD83D\uDCD3. This milestone was " +
                        "all about creating the first impression \uD83D\uDCAB and ensuring that the project has a clean," +
                        " efficient base to grow on \uD83C\uDF31. On to the next phase—member profiles and event " +
                        "registration!")
                .estimatedHours(80)
                .build();


        Milestone milestone2 = Milestone.builder()
                .name("milestone 2")
                .number(2)
                .description("\uD83D\uDE80 The first milestone of our club website development is officially complete! " +
                        "\uD83E\uDD73 We kicked things off by laying down a strong foundation " +
                        "\uD83E\uDDF1—starting with a fully responsive homepage \uD83D\uDDA5\uFE0F\uD83D\uDCF1 that" +
                        " captures the club’s identity \uD83C\uDFAF. We implemented smooth navigation \uD83D\uDD17, " +
                        "a sleek UI design ✨, and an engaging landing section that tells our story \uD83D\uDCD6. " +
                        "We also integrated a basic member login system \uD83D\uDD10 to prepare for future personalized " +
                        "features. The homepage is now live with sections like About Us \uD83D\uDC65, Events \uD83D\uDCC5, " +
                        "and Contact ✉\uFE0F—each built with accessibility and performance in mind ⚙\uFE0F. Behind the" +
                        " scenes, we set up our codebase on GitHub \uD83E\uDDD1\u200D\uD83D\uDCBB\uD83D\uDCC1, organized " +
                        "our components for scalability \uD83D\uDCD0, and documented everything for future developers" +
                        " \uD83D\uDCD3. This milestone was all about creating the first impression \uD83D\uDCAB and " +
                        "ensuring that the project has a clean, efficient base to grow on \uD83C\uDF31. On to the next " +
                        "phase—member profiles and event registration!")
                .status(Milestone.MilestoneStatus.NOT_STARTED)
                .dueDate(new Date())
                .estimatedHours(90)
                .build();

        ArrayList<Milestone> listmilestone2 = new ArrayList<>(List.of(milestone1, milestone2));
        Client client02 =clientRepository.findByUser(userRepository.findByUsername("client02").get()).get();

        Contract activeContract = Contract.builder()
                .client(client02)
                .job(activeJob)
                .workerEntity(pabloCommunity.getWorkerEntity())
                .costPerHour(20d)
                .status(Contract.ContractStatus.PENDING)
                .payment(PaymentMethod.PerMilestones)
                .milestones(listmilestone2)
                .build();

        activeJob.setContract(activeContract);
        jobRepository.save(activeJob);
        Proposal proposal = Proposal.builder()
                .costPerHour(20d)
                .date(new Date())
                .milestones(listmilestone2)
                .contract(activeContract)
                .client(client02)
                .status(Proposal.ProposalStatus.HIRED)
                .job(activeJob)
                .payment(PaymentMethod.PerMilestones)
                .workerEntity(pabloCommunity.getWorkerEntity())
                .coverLetter("please accept me")
                .build();
        proposalRepository.save(proposal);
        contractRepository.save(activeContract);
        contractService.startContract(activeContract, false);
        System.out.println("club contract Active contract id: " + activeContract.getId());

        contractRepository.save(activeContract);
    }


    public void communityJoinRequests(){
        Community pabloCommunity = communityRepository.findByCommunityName("Pablo Community").get();

        User user88 = User.builder()
                .email("mohib@gmail.com")
                .password(new BCryptPasswordEncoder().encode("Mohib@123"))
                .username("Mohib")
                .role(User.Role.FREELANCER)
                .build();

        User user99 = User.builder()
                .email("jolieattallah@gmail.com")
                .password(new BCryptPasswordEncoder().encode("Jolie@123"))
                .username("Jolie")
                .role(User.Role.FREELANCER)
                .build();

        userRepository.save(user88);
        userRepository.save(user99);

        Freelancer freelancer88 = Freelancer.builder()
                .name("Mohib")
                .user(user88)
                .pricePerHour(45.5)
                .rate(3.5F)
                .profilePicture(Constants.FIRST_PROFILE_PICTURE)
                .experienceLevel(ExperienceLevel.entry_level)
                .freelancerBusiness(new FreelancerBusiness())
                .build();
        Freelancer freelancer99 = Freelancer.builder()
                .name("Jolie")
                .user(user99)
                .pricePerHour(45.5)
                .freelancerBusiness(new FreelancerBusiness())
                .balance(new FreelancerBalance())
                .rate(5.0F)
                .profilePicture(Constants.FIRST_PROFILE_PICTURE)
                .experienceLevel(ExperienceLevel.entry_level)
                .build();

        freelancerRepository.save(freelancer99);
        freelancerRepository.save(freelancer88);

        CommunityMember communityMember1 = CommunityMember.builder()
                .community(pabloCommunity)
                .positionName("AI developer")
                .positionPercent(20)
                .description("AI implementation and integration")
                .build();

        communityMemberService.saveMember(communityMember1);
        System.out.println("Pablo's community open position id: " + communityMember1.getId());

        CommunityMember communityMember2 = CommunityMember.builder()
                .community(pabloCommunity)
                .positionName("Frontend developer")
                .positionPercent(20)
                .description("Interface design and implementation")
                .build();

        communityMemberService.saveMember(communityMember2);
        System.out.println("Pablo's community open position 2 id: " + communityMember2.getId());

        JoinRequest joinRequest1 = JoinRequest.builder()
                .community(pabloCommunity)
                .freelancer(freelancerRepository.findByUser(userRepository.findByUsername("Mohib").get()).get())
                .position(communityMember1)
                .build();

        JoinRequest joinRequest2 = JoinRequest.builder()
                .community(pabloCommunity)
                .freelancer(freelancerRepository.findByUser(userRepository.findByUsername("Jolie").get()).get())
                .position(communityMember1)
                .build();

        JoinRequest joinRequest3 = JoinRequest.builder()
                .community(pabloCommunity)
                .freelancer(freelancerRepository.findByUser(userRepository.findByUsername("freelancer04").get()).get())
                .position(communityMember2)
                .build();
        communityJoinRequestRepository.save(joinRequest1);
        communityJoinRequestRepository.save(joinRequest2);
        communityJoinRequestRepository.save(joinRequest3);
        System.out.println("Jolie id:" + freelancer99.getId());
    }

    public void communityPost(){
        Community community = communityRepository.findByCommunityName("Pablo Community").get();
        Freelancer freelancer02 = freelancerRepository.findByUser(userRepository.findByUsername("freelancer02").get()).get();
        Freelancer freelancer01 = freelancerRepository.findByUser(userRepository.findByUsername("freelancer01").get()).get();

        Post post1 = Post.builder()
                .communityId(community.getUuid().toString())
                .content("Hello everyone, welcome to our community! Let's connect and share ideas.")
                .title("Welcome Post!")
                .ownerId(freelancer02.getId().toString())
                .createdAt(new Date())
                .build();

        Post post2 = Post.builder()
                .communityId(community.getUuid().toString())
                .title("New Project!")
                .content("Exciting news! We have a new project coming up. Stay tuned for details.")
                .ownerId(freelancer01.getId().toString())
                .createdAt(new Date())
                .build();

        communityPostRepository.save(post1);
        communityPostRepository.save(post2);
    }

    public void communityPostLikesAndComments(){
        Community community = communityRepository.findByCommunityName("Pablo Community").get();
        List<Post> posts = communityPostRepository.findByCommunityId(community.getUuid().toString());
        Freelancer freelancer02 = freelancerRepository.findByUser(userRepository.findByUsername("freelancer02").get()).get();
        Freelancer freelancer01 = freelancerRepository.findByUser(userRepository.findByUsername("freelancer01").get()).get();
        Freelancer freelancer03 = freelancerRepository.findByUser(userRepository.findByUsername("freelancer03").get()).get();

        posts.get(0).getLikerId().add(freelancer01.getId().toString());
        posts.get(0).getLikerId().add(freelancer03.getId().toString());

        posts.get(1).getLikerId().add(freelancer02.getId().toString());
        posts.get(1).getLikerId().add(freelancer03.getId().toString());

        PostComment comment1 = PostComment.builder()
                .postId(posts.get(0).getId())
                .content("This is a great start! I'm excited!!")
                .ownerId(freelancer03.getId().toString())
                .createdAt(new Date())
                .build();
        communityPostCommentRepository.save(comment1);
        posts.get(0).getCommentId().add(comment1.getId());
        communityPostRepository.save(posts.get(0));

        for(int i=2; i<13;i++){
            PostComment comment = PostComment.builder()
                    .postId(posts.get(0).getId())
                    .content("This is comment number "+i+" !!")
                    .ownerId(freelancer03.getId().toString())
                    .createdAt(new Date())
                    .build();
            communityPostCommentRepository.save(comment);
            posts.get(0).getCommentId().add(comment.getId());
            communityPostRepository.save(posts.get(0));
        }

        PostComment comment2 = PostComment.builder()
                .postId(posts.get(1).getId())
                .content("Let's go for it!")
                .ownerId(freelancer03.getId().toString())
                .createdAt(new Date())
                .build();
        communityPostCommentRepository.save(comment2);
        posts.get(1).getCommentId().add(comment2.getId());
        communityPostRepository.save(posts.get(1));
    }

}
