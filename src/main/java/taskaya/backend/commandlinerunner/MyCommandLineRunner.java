package taskaya.backend.commandlinerunner;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.milestones.requests.MilestoneSubmitProposalRequestDTO;
import taskaya.backend.DTO.proposals.requests.SubmitProposalRequestDTO;
import taskaya.backend.config.Constants;
import taskaya.backend.entity.Payment;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.entity.community.JoinRequest;
import taskaya.backend.entity.community.Vote;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.enums.PaymentMethod;
import taskaya.backend.entity.enums.ProjectLength;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.freelancer.FreelancerPortfolio;
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
import taskaya.backend.repository.work.ContractRepository;
import taskaya.backend.repository.work.JobRepository;
import taskaya.backend.repository.work.ProposalRepository;
import taskaya.backend.repository.work.WorkerEntityRepository;
import taskaya.backend.services.CloudinaryService;
import taskaya.backend.services.client.ClientService;
import taskaya.backend.services.community.CommunityMemberService;
import taskaya.backend.services.community.CommunityService;
import taskaya.backend.services.freelancer.FreelancerService;
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

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        skillSeed();
        freelancersInitializer.freelancerSeed();
        clientsInitializer.clientSeed();
        jobsInitializer.jobSeed();
        communitiesInitializer.communityWithAdmin();
        seedCommunityAndCommunityMember();
//		proposalSeed();
        freelancersInitializer.freelancerWorkdoneWithPaymentseed();
        communityWithContractPendingAndVotes();
        communitiesInitializer.communityWorkdoneSeed();
    }

    private void communityWithContractPendingAndVotes() {
        Community pabloCommunity = communityRepository.findByCommunityName("Pablo Community").get();
        Milestone milestone1 = Milestone.builder()
                .name("milestone11")
                .number(61)
                .status(Milestone.MilestoneStatus.NOT_STARTED)
                .dueDate(new Date())
                .estimatedHours(7545)
                .build();

        List<Milestone> listmilestone = new ArrayList<>();
        List<Milestone>listmilestone2 = new ArrayList<>();
        listmilestone.add(milestone1);
        listmilestone2.add(milestone1);

        Contract pendingContract = Contract.builder()
                .client(clientRepository.findByUser(userRepository.findByUsername("client01").get()).get())
                .job(jobRepository.findByTitle("job1").get())
                .workerEntity(pabloCommunity.getWorkerEntity())
                .costPerHour(45.77)
                .status(Contract.ContractStatus.PENDING)
                .payment(PaymentMethod.PerProject)
                .milestones(listmilestone)
                .build();


        Contract activeContract = Contract.builder()
                .client(clientRepository.findByUser(userRepository.findByUsername("client02").get()).get())
                .job(jobRepository.findByTitle("job2").get())
                .workerEntity(pabloCommunity.getWorkerEntity())
                .costPerHour(458.77)
                .status(Contract.ContractStatus.ACTIVE)
                .payment(PaymentMethod.PerProject)
                .milestones(listmilestone2)
                .build();

        CommunityMember person1 = pabloCommunity.getCommunityMembers().get(0);
        System.out.println("person1 :" + person1.getFreelancer().getName());
        CommunityMember person2 = pabloCommunity.getCommunityMembers().get(1);
        System.out.println("person2 :" + person2.getFreelancer().getName());
        CommunityMember person3 = pabloCommunity.getCommunityMembers().get(2);
        System.out.println("person3 :" + person3.getFreelancer().getName());

        contractRepository.save(pendingContract);
        System.out.println("Contact id:"+pendingContract.getId());
        contractRepository.save(activeContract);

        Vote yesVote = Vote.builder()
                .contract(pendingContract)
                .communityMember(person1)
                .agreed(null)
                .build();

        Vote noVote = Vote.builder()
                .contract(pendingContract)
                .communityMember(person2)
                .agreed(null)
                .build();

        Vote nullVote = Vote.builder()
                .contract(pendingContract)
                .communityMember(person3)
                .agreed(null)
                .build();


        communityVoteRepository.save(yesVote);
        communityVoteRepository.save(noVote);
        communityVoteRepository.save(nullVote);

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
                .rate(55)
                .profilePicture(Constants.FIRST_PROFILE_PICTURE)
                .experienceLevel(ExperienceLevel.entry_level)
                .build();
        Freelancer freelancer99 = Freelancer.builder()
                .name("Jolie")
                .user(user99)
                .pricePerHour(45.5)
                .rate(55)
                .profilePicture(Constants.FIRST_PROFILE_PICTURE)
                .experienceLevel(ExperienceLevel.entry_level)
                .build();

        freelancerRepository.save(freelancer99);
        freelancerRepository.save(freelancer88);

        CommunityMember communityMember =CommunityMember.builder()
                .community(pabloCommunity)
                .positionName("clown")
                .positionPercent(9)
                .build();

        communityMemberService.saveMember(communityMember);

        JoinRequest joinRequest1 = JoinRequest.builder()
                .community(pabloCommunity)
                .freelancer(freelancerRepository.findByUser(userRepository.findByUsername("Mohib").get()).get())
                .position(communityMember)
                .build();

        JoinRequest joinRequest2 = JoinRequest.builder()
                .community(pabloCommunity)
                .freelancer(freelancerRepository.findByUser(userRepository.findByUsername("Jolie").get()).get())
                .position(communityMember)
                .build();

        communityJoinRequestRepository.save(joinRequest1);
        communityJoinRequestRepository.save(joinRequest2);
        System.out.println("Jolie id:"+freelancer99.getId());
    }



    private void proposalSeed() throws MessagingException, IOException {
		/*
		NO ATTACHMENT ASSIGNED IN THIS TEST FUNCTION
		*/
        User user1 = userRepository.findByUsername("client02").get();
        Client client1 = clientRepository.findByUser(user1).get();

        Job myJob = Job.builder()
                .title("MY TEST JOBBBB FOR PROPOSAL")
                .client(client1)
                .experienceLevel(ExperienceLevel.expert)
                .projectLength(ProjectLength._more_than_6_months)
                .status(Job.JobStatus.NOT_ASSIGNED)
                .description("Seeking a microservices expert for API development using Spring Boot and Kafka.")
                .pricePerHour(60)
                .build();

        jobRepository.save(myJob);
        myJob = jobRepository.findByTitle("MY TEST JOBBBB FOR PROPOSAL")
                .orElseThrow(()->new RuntimeException("Job Not Found!"));


        MilestoneSubmitProposalRequestDTO myMilestone1 = MilestoneSubmitProposalRequestDTO.builder()
                .title("Milestone 1")
                .description("This is the First Milestone")
                .dueDate(new Date())
                .expectedHours(20)
                .milestoneNumber(1)
                .build();

        MilestoneSubmitProposalRequestDTO myMilestone2 = MilestoneSubmitProposalRequestDTO.builder()
                .title("Milestone 2")
                .description("This is the Second Milestone")
                .dueDate(new Date())
                .expectedHours(10)
                .milestoneNumber(2)
                .build();

        List<MilestoneSubmitProposalRequestDTO> milestoneList = new ArrayList<>(Arrays.asList(myMilestone1,myMilestone2));

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

        proposalService.createProposal(submitProposalRequestDTO,myJob.getUuid());

        WorkerEntity commWorkerEntity = communityService.getCommunityByName("mina Community").getWorkerEntity();

        SubmitProposalRequestDTO commRequestDTO = SubmitProposalRequestDTO.builder()
                .jobId(myJob.getUuid().toString())
                .candidateId(commWorkerEntity.getId().toString()) //workerEntity ID
                .pricePerHour(14.6)
                .freelancerPayment(PaymentMethod.PerProject)
                .coverLetter("Community Cover Letter")
                .milestones(milestoneList)
                .build();

        proposalService.createProposal(commRequestDTO,myJob.getUuid());
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
                .avrgHoursPerWeek(6)
                .pricePerHour(35)
                .status(Community.CommunityStatus.AVAILABLE)
                .rate(3)
                .skills(new HashSet<>(skills))
                .description("This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community"+"This is the "+ name +" Community")
                .isFull(false)
                .experienceLevel(exp)
                .build();

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





}