package taskaya.backend.commandlinerunner;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.milestones.requests.MilestoneSubmitRequestDTO;
import taskaya.backend.DTO.proposals.requests.SubmitProposalRequestDTO;
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
import taskaya.backend.repository.community.CommunityJoinRequestRepository;
import taskaya.backend.repository.community.CommunityMemberRepository;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.community.CommunityVoteRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.repository.work.*;
import taskaya.backend.services.CloudinaryService;
import taskaya.backend.services.client.ClientService;
import taskaya.backend.services.community.CommunityMemberService;
import taskaya.backend.services.community.CommunityService;
import taskaya.backend.services.freelancer.FreelancerService;
import taskaya.backend.services.work.ContractService;
import taskaya.backend.services.work.ProposalService;
import taskaya.backend.services.work.WorkerEntityService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
class MyCommandLineRunner implements CommandLineRunner {

    @Autowired
    FreelancersInitializer freelancersInitializer;
    @Autowired
    ClientsInitializer clientsInitializer;
    @Autowired
    JobsInitializer jobsInitializer;
    @Autowired
    CommunitiesInitializer communitiesInitializer;


    @Autowired
    FreelancerService freelancerService;
    @Autowired
    FreelancerRepository freelancerRepository;
    @Autowired
    ClientService clientService;
    @Autowired
    SkillRepository skillRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JobRepository jobRepository;
    @Autowired
    WorkerEntityRepository workerEntityRepository;
    @Autowired
    WorkerEntityService workerEntityService;

    @Autowired
    CommunityService communityService;

    @Autowired
    CommunityMemberService communityMemberService;

    @Autowired
    ProposalService proposalService;

    @Autowired
    ProposalRepository proposalRepository;

    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    ContractRepository contractRepository;

    @Autowired
    CommunityRepository communityRepository;

    @Autowired
    CommunityJoinRequestRepository communityJoinRequestRepository;

    @Autowired
    CommunityMemberRepository communityMemberRepository;

    @Autowired
    CommunityVoteRepository communityVoteRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    MilestoneRepository milestoneRepository;

    @Autowired
    ContractService contractService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        mongoTemplate.dropCollection("post");
        mongoTemplate.dropCollection("postcomment");

        skillSeed();
        freelancersInitializer.freelancerSeed();
        clientsInitializer.clientSeed();
        jobsInitializer.jobSeed();
        communitiesInitializer.communityWithAdmin();
        seedCommunityAndCommunityMember();
//		proposalSeed();
        freelancersInitializer.freelancerWorkdoneWithPaymentseed();
        communitiesInitializer.communityWithContractPendingAndVotes();
        communitiesInitializer.communityWithActiveJob();
        communitiesInitializer.communityJoinRequests();
        communitiesInitializer.communityWorkdoneSeed();
        communitiesInitializer.communityWithMinaAdmin();
        freelancersInitializer.freelancerWorkInProgressSeed();
        mileStoneRequestReview();
        communitiesInitializer.communityPost();
        communitiesInitializer.communityPostLikesAndComments();

        freelancersInitializer.freelancerPendingProposalSeed();

    }




    private void proposalSeed() throws MessagingException, IOException {
		/*
		NO ATTACHMENT ASSIGNED IN THIS TEST FUNCTION
		*/
        User user1 = userRepository.findByUsername("client02").get();
        Client client1 = clientRepository.findByUser(user1).get();

        List<String> skillNames1 = List.of("Java", "Spring Boot", "Spring Security", "Spring Data JPA", "Hibernate");
        List<Skill> skills1 = skillRepository.findByNameIn(skillNames1);

        Job myJob = Job.builder()
                .title("MY TEST JOBBBB FOR PROPOSAL")
                .client(client1)
                .experienceLevel(ExperienceLevel.expert)
                .projectLength(ProjectLength._more_than_6_months)
                .status(Job.JobStatus.NOT_ASSIGNED)
                .skills(new HashSet<>(skills1))
                .description("Seeking a microservices expert for API development using Spring Boot and Kafka.")
                .pricePerHour(60)
                .build();

        jobRepository.save(myJob);
        myJob = jobRepository.findByTitle("MY TEST JOBBBB FOR PROPOSAL")
                .orElseThrow(()->new RuntimeException("Job Not Found!"));


        MilestoneSubmitRequestDTO myMilestone1 = MilestoneSubmitRequestDTO.builder()
                .title("Milestone 1")
                .description("This is the First Milestone")
                .dueDate(new Date())
                .expectedHours(20)
                .milestoneNumber(1)
                .build();

        MilestoneSubmitRequestDTO myMilestone2 = MilestoneSubmitRequestDTO.builder()
                .title("Milestone 2")
                .description("This is the Second Milestone")
                .dueDate(new Date())
                .expectedHours(10)
                .milestoneNumber(2)
                .build();

        List<MilestoneSubmitRequestDTO> milestoneList = new ArrayList<>(Arrays.asList(myMilestone1,myMilestone2));

        User user = User.builder()
                .email("markosama@gmail.com")
                .role(User.Role.FREELANCER)
                .username("markOsama10")
                .password(new BCryptPasswordEncoder().encode("Freelancer2@123"))
                .build();
        freelancerService.createFreelancer(user);

        WorkerEntity workerEntity = freelancerService.getById(user.getId()).getWorkerEntity();


        SubmitProposalRequestDTO submitProposalRequestDTO = SubmitProposalRequestDTO.builder()
                .jobId(myJob.getUuid().toString())
                .candidateId(workerEntity.getId().toString()) //workerEntity ID
                .pricePerHour(14.6)
                .freelancerPayment(PaymentMethod.PerMilestones)
                .coverLetter("Please accept my proposal, I need money :_)")
                .milestones(milestoneList)
                .build();

        proposalService.createProposal(submitProposalRequestDTO,myJob.getUuid(),false);

        WorkerEntity commWorkerEntity = communityService.getCommunityByName("mina Community").getWorkerEntity();

        SubmitProposalRequestDTO commRequestDTO = SubmitProposalRequestDTO.builder()
                .jobId(myJob.getUuid().toString())
                .candidateId(commWorkerEntity.getId().toString()) //workerEntity ID
                .pricePerHour(14.6)
                .freelancerPayment(PaymentMethod.PerProject)
                .coverLetter("Community Cover Letter")
                .milestones(milestoneList)
                .build();

        proposalService.createProposal(commRequestDTO,myJob.getUuid(),false);
    }

    public void seedCommunityAndCommunityMember(){
        createCommunityAndMember("mina",ExperienceLevel.expert);
//		createCommunityAndMember("mark",ExperienceLevel.expert);
//		createCommunityAndMember("omar",ExperienceLevel.entry_level);
//		createCommunityAndMember("mohamed",ExperienceLevel.entry_level);
//		createCommunityAndMember("andrew",ExperienceLevel.intermediate);
//		createCommunityAndMember("martin",ExperienceLevel.intermediate);
    }

    public void createCommunityAndMember(String name, ExperienceLevel exp){
        User user = User.builder()
                .username(name)
                .email(name+"@gmail.com")
                .password(new BCryptPasswordEncoder().encode(name+"@765"))
                .role(User.Role.FREELANCER)
                .build();

        freelancerService.createFreelancer(user);

        List<String> mySkills = List.of("Java", "Spring Boot", "Spring Security", "Spring Data JPA", "Hibernate");
        List<Skill> skills = skillRepository.findByNameIn(mySkills);

        WorkerEntity workerEntity = WorkerEntity.builder()
                .type(WorkerEntity.WorkerType.COMMUNITY)
                .build();
        workerEntityService.createWorkerEntity(workerEntity);

//		System.out.println("Test Community ID: "+workerEntity.getId());
//		System.out.println("Test Freelancer ID: "+freelancerService.getById(user.getId()).getWorkerEntity().getId());

        System.out.println("mina comm leader :"+ freelancerService.getById(user.getId()).getId());
        Community community = Community.builder()
                .communityName(name+" Community")
                .title("This is mina community title!")
                .description("This is mina's description!")
                .country("Benseuf")
                .admin(freelancerService.getById(user.getId()))
                .workerEntity(workerEntity)
                .pricePerHour(35)
                .status(Community.CommunityStatus.AVAILABLE)
                .rate(3)
                .skills(new HashSet<>(skills))
                .description("This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community")
                .isFull(false)
                .experienceLevel(exp)
                .build();

        community.getFreelancerBusiness().setAvgHoursPerWeek(600);

        CommunityMember communityMember1 = CommunityMember.builder()
                .positionPercent(40.1F)
                .positionName("adminN")
                .community(community)
                .freelancer(freelancerService.getById(user.getId()))
                .build();

        community.getCommunityMembers().add(communityMember1);

        communityService.save(community);
        community = communityService.getCommunityByName(name+" Community");

        System.out.println("Mina Community ID: " + community.getUuid());

        CommunityMember communityMember = CommunityMember.builder()
                .community(community)
                .positionName("Admin")
                .positionPercent(4)
                .freelancer(freelancerService.getById(user.getId()))
                .build();

        CommunityMember communityMember2 = CommunityMember.builder()
                .community(community)
                .positionName("member1")
                .positionPercent(5)
                .freelancer(freelancerRepository.findByUser(userRepository.findByUsername("freelancer03").orElseThrow()).orElseThrow())
                .build();

//		communityMemberService.addMember(communityMember);
//		communityMemberService.addMember(communityMember2);
//		communityMember = communityMemberService.findById(1);
//		communityMember2 = communityMemberService.findById(2);
        community.getCommunityMembers().add(communityMember);
        community.getCommunityMembers().add(communityMember2);

        communityService.save(community);
    }



    private void skillSeed() {
        List<String> skills = List.of(
                // 💻 Software Development & Programming
                "Java", "Spring Boot", "Spring Security", "Spring Data JPA", "Hibernate", "Microservices", "REST API",
                "GraphQL", "gRPC", "Python", "Django", "Flask", "FastAPI", "JavaScript", "TypeScript", "Node.js",
                "Express.js", "React.js", "Angular", "Vue.js", "Next.js", "Nuxt.js", "Svelte", "Solid.js",
                "Remix", "Qwik", "WebSockets", "HTML", "CSS", "Tailwind CSS", "Bootstrap", "SCSS", "Less",
                "PHP", "Laravel", "Symfony", "Ruby on Rails", "Go", "Rust", "Scala", "Kotlin", "Swift", "Objective-C",
                "Dart", "Flutter", "React Native", "Ionic", "C", "C++", "C#", ".NET", "F#", "Assembly", "COBOL",
                "Fortran", "R", "Matlab", "Julia", "Haskell", "Elixir", "Erlang", "Lisp", "Prolog", "VHDL",
                "Verilog", "Embedded Systems", "Arduino", "Raspberry Pi", "Quantum Computing", "Web3.js",
                "Blockchain", "Ethereum", "Smart Contracts", "Solidity", "Rust for Blockchain", "Substrate", "Bitcoin",

                // 🗄 Databases
                "SQL", "PostgreSQL", "MySQL", "MariaDB", "SQLite", "Oracle DB", "Microsoft SQL Server", "MongoDB",
                "Firebase", "Cassandra", "Neo4j", "DynamoDB", "Redis", "Elasticsearch", "InfluxDB", "Supabase",

                // ☁ Cloud & DevOps
                "AWS", "Google Cloud", "Azure", "DigitalOcean", "Cloudflare", "Terraform", "Ansible", "Kubernetes",
                "Docker", "Helm", "Jenkins", "GitHub Actions", "GitLab CI/CD", "Travis CI", "CircleCI", "ArgoCD",
                "Pulumi", "Vagrant", "Serverless Framework", "Cloud Functions", "Lambda", "Kafka", "RabbitMQ",

                // 🤖 AI, Data Science & Machine Learning
                "Machine Learning", "Deep Learning", "Natural Language Processing (NLP)", "Computer Vision", "TensorFlow",
                "PyTorch", "Scikit-learn", "Keras", "OpenCV", "Hugging Face Transformers", "Reinforcement Learning",
                "Big Data", "Apache Spark", "Apache Kafka", "Dask", "Hadoop", "Airflow", "Data Engineering",
                "ETL Pipelines", "SQL Optimization", "Data Visualization", "Matplotlib", "Seaborn", "Tableau", "Power BI",

                // 🎨 UI/UX & Graphic Design
                "UI/UX Design", "Wireframing", "Prototyping", "Adobe XD", "Figma", "Sketch", "Adobe Photoshop",
                "Adobe Illustrator", "CorelDRAW", "Inkscape", "Canva", "Motion Graphics", "Video Editing",
                "Adobe Premiere Pro", "After Effects", "Final Cut Pro", "Cinema 4D", "Blender", "Maya",

                // ✍ Content Creation & Writing
                "Content Writing", "Copywriting", "Technical Writing", "Ghostwriting", "SEO Writing", "Creative Writing",
                "Proofreading", "Translation", "Transcription", "Blogging", "Academic Writing",

                // 📢 Digital Marketing
                "SEO", "Google Analytics", "Google Ads", "Facebook Ads", "Instagram Ads", "TikTok Ads", "LinkedIn Ads",
                "Affiliate Marketing", "Email Marketing", "Social Media Management", "Growth Hacking", "Influencer Marketing",

                // 🏢 Business & Management
                "Business Analysis", "Product Management", "Agile Methodologies", "Scrum", "Kanban", "Lean Startup",
                "OKRs", "Project Management", "Risk Management", "Financial Analysis", "Investment Analysis",
                "Stock Market Analysis", "Cryptocurrency Trading", "Real Estate Investment",

                // 🤝 Customer Support & Virtual Assistance
                "Customer Support", "Technical Support", "Virtual Assistance", "Data Entry", "Live Chat Support",
                "Cold Calling", "Lead Generation", "CRM Software", "Zendesk", "HubSpot", "Salesforce",

                // 🎵 Music & Audio Production
                "Music Production", "Sound Engineering", "Audio Editing", "Podcast Editing", "Mixing & Mastering",
                "FL Studio", "Ableton Live", "Logic Pro", "GarageBand", "Cubase", "Pro Tools",

                // 🛠 Miscellaneous
                "3D Printing", "Game Development", "Unreal Engine", "Unity", "Godot", "Penetration Testing", "Cybersecurity",
                "Ethical Hacking", "OSINT", "Reverse Engineering", "Metasploit", "Bug Bounty Hunting", "Linux Administration"
        );

        List<Skill> skillEntities = skills.stream()
                .filter(skill -> skillRepository.findByName(skill).isEmpty()) // Avoid duplicates
                .map(skillName -> new Skill(skillName))
                .collect(Collectors.toList());

        if (!skillEntities.isEmpty()) {
            skillRepository.saveAll(skillEntities);
            System.out.println("✅ " + skillEntities.size() + " Skills inserted into database!");
        } else {
            System.out.println("✅ Skills already exist in database.");
        }


    }

    public void mileStoneRequestReview(){

        User userC = userRepository.findByUsername("client01").get();
        Client client = clientRepository.findByUser(userC).get();

        User userF = userRepository.findByUsername("freelancer03").get();
        Freelancer freelancer = freelancerRepository.findByUser(userF).get();
        WorkerEntity workerEntity = freelancer.getWorkerEntity();

        List<String> skillNames1 = List.of("Java", "Spring Boot", "Spring Security", "Spring Data JPA", "Hibernate");
        List<Skill> skills1 = skillRepository.findByNameIn(skillNames1);

        Job job = Job.builder()
                .title("job with a pending mileStone.")
                .client(client)
                .skills(new HashSet<>(skills1))
                .assignedTo(workerEntity)
                .status(Job.JobStatus.IN_PROGRESS)
                .description("This is a job with a pending mileStone to test the requestReview feature.")
                .postedAt(new Date())
                .experienceLevel(ExperienceLevel.entry_level)
                .pricePerHour(565)
                .projectLength(ProjectLength._more_than_6_months)
                .build();

        ArrayList<Milestone> milestones = new ArrayList<>();

        milestones.add(Milestone.builder()
                .name("Project Kickoff")
                .startDate(new Date())
                .dueDate(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)) // 7 days later
                .endDate(null)
                .number(1)
                .description("Initial meeting and project planning.")
                .estimatedHours(10)
                .status(Milestone.MilestoneStatus.IN_PROGRESS)
                .build());

        milestones.add(Milestone.builder()
                .name("Design Phase")
                .startDate(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)) // Starts in a week
                .dueDate(new Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000)) // Due in 2 weeks
                .endDate(null)
                .number(2)
                .description("Creating wireframes and design mockups.")
                .estimatedHours(20)
                .status(Milestone.MilestoneStatus.NOT_STARTED)
                .build());

        milestones.add(Milestone.builder()
                .name("Development Phase")
                .startDate(new Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000)) // Starts in 2 weeks
                .dueDate(new Date(System.currentTimeMillis() + 28L * 24 * 60 * 60 * 1000)) // Due in 4 weeks
                .endDate(null)
                .number(3)
                .description("Implementing core features and functionality.")
                .estimatedHours(50)
                .status(Milestone.MilestoneStatus.NOT_STARTED)
                .build());

        milestones.add(Milestone.builder()
                .name("Testing & QA")
                .startDate(new Date(System.currentTimeMillis() + 28L * 24 * 60 * 60 * 1000)) // Starts in 4 weeks
                .dueDate(new Date(System.currentTimeMillis() + 35L * 24 * 60 * 60 * 1000)) // Due in 5 weeks
                .endDate(null)
                .number(4)
                .description("Testing, debugging, and quality assurance.")
                .estimatedHours(30)
                .status(Milestone.MilestoneStatus.NOT_STARTED)
                .build());

        milestones.add(Milestone.builder()
                .name("Project Delivery")
                .startDate(new Date(System.currentTimeMillis() + 35L * 24 * 60 * 60 * 1000)) // Starts in 5 weeks
                .dueDate(new Date(System.currentTimeMillis() + 42L * 24 * 60 * 60 * 1000)) // Due in 6 weeks
                .endDate(null)
                .number(5)
                .description("Final project handover and documentation.")
                .estimatedHours(15)
                .status(Milestone.MilestoneStatus.NOT_STARTED)
                .build());



        Contract contract = Contract.builder()
                .job(job)
                .workerEntity(workerEntity)
                .client(client)
                .description("This is a contract for the job with a pending mileStone to requestReview feature.")
                .costPerHour(45.8)
                .status(Contract.ContractStatus.ACTIVE)
                .milestones(milestones)
                .payment(PaymentMethod.PerMilestones)
                .build();



        job.setContract(contract);
        jobRepository.save(job);
        contractRepository.save(contract);
        System.out.println("Milestone request review contract id: "+contract.getId().toString());

    }
}