package taskaya.backend.commandlinerunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import taskaya.backend.config.Constants;
import taskaya.backend.entity.Payment;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.enums.PaymentMethod;
import taskaya.backend.entity.enums.ProjectLength;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.freelancer.FreelancerBalance;
import taskaya.backend.entity.freelancer.FreelancerPortfolio;
import taskaya.backend.entity.work.*;
import taskaya.backend.repository.PaymentRepository;
import taskaya.backend.repository.SkillRepository;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.client.ClientRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.repository.work.ContractRepository;
import taskaya.backend.repository.work.JobRepository;
import taskaya.backend.repository.work.ProposalRepository;
import taskaya.backend.services.freelancer.FreelancerService;

import java.util.*;

@Component
public class FreelancersInitializer {

    @Autowired
    private FreelancerRepository freelancerRepository;
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private FreelancerService freelancerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ProposalRepository proposalRepository;


    public void freelancerSeed(){

        User freelancerUser1 = User.builder()
                .email("martin619100@gmail.com")
                .role(User.Role.FREELANCER)
                .username("freelancer01")
                .password(new BCryptPasswordEncoder().encode("Freelancer1@123"))
                .build();


        List<String> skillNames1 = List.of("Java", "Spring Boot", "Spring Security", "Spring Data JPA", "Hibernate");
        List<Skill> skills1 = skillRepository.findByNameIn(skillNames1);


        User freelancerUser2 = User.builder()
                .email("minahany910@gmail.com")
                .role(User.Role.FREELANCER)
                .username("freelancer02")
                .password(new BCryptPasswordEncoder().encode("Freelancer2@123"))
                .build();

        List<String> skillNames2 = List.of("AWS", "Google Cloud", "Azure");
        List<Skill> skills2 = skillRepository.findByNameIn(skillNames2);


        User freelancerUser3 = User.builder()
                .email("mauricelara02@gmail.com")
                .role(User.Role.FREELANCER)
                .username("freelancer03")
                .password(new BCryptPasswordEncoder().encode("Freelancer3@123"))
                .build();
        List<String> skillNames3 = List.of("Machine Learning", "Deep Learning", "Natural Language Processing (NLP)");
        List<Skill> skills3 = skillRepository.findByNameIn(skillNames3);

        Freelancer freelancer1=freelancerService.createFreelancer(freelancerUser1);
        Freelancer freelancer2=freelancerService.createFreelancer(freelancerUser2);
        Freelancer freelancer3=freelancerService.createFreelancer(freelancerUser3);

        List<FreelancerPortfolio> portfolios= new ArrayList<>();
        portfolios.add(FreelancerPortfolio.builder()
                .portfolioPdf("https://res.cloudinary.com/dhfb7i5h1/image/upload/v1741044338/freelancer_portfolios/22bd3c8b-a0d6-4a2b-9d1c-8814832c312cmartinCV.pdf")
                .name("mycv")
                .build());

        freelancer1.setSkills(new HashSet<>(skills1));
        freelancer1.setPricePerHour(50D);
        freelancer1.setRate(5);
        freelancer1.setDescription("best freelancer you will deal with ;)");
        freelancer1.setTitle("fullstack developer");
        freelancer1.setCountry("cairo,Egypt");
        freelancer1.setName("martin ashraf");
        freelancer1.setProfilePicture("https://res.cloudinary.com/dhfb7i5h1/image/upload/v1740135952/freelancers_profile_pictures/q9oe51olxf4ohwm8mlis.jpg");
        freelancer1.setLanguages(new HashSet<>(List.of("english")));
        freelancer1.setPortfolios(portfolios);

        FreelancerBalance freelancerBalance1 = freelancer1.getBalance();
        freelancerBalance1.setAvailable(10000.0);
        freelancerBalance1.setWorkInProgress(2000.0);

        freelancerRepository.save(freelancer1);


        freelancer2.setSkills(new HashSet<>(skills2));
        freelancer2.setPricePerHour(60D);
        freelancer2.setRate(4);
        freelancer2.setDescription("best freelancer you will deal with ;)");
        freelancer2.setTitle("fullstack developer");
        freelancer2.setCountry("cairo,Egypt");
        freelancer2.setName("mina hany");
        freelancer2.setProfilePicture("https://res.cloudinary.com/dhfb7i5h1/image/upload/v1740614562/freelancers_profile_pictures/nf173ownjkrkm24qioi7.jpg");
        freelancer2.setLanguages(new HashSet<>(List.of("english")));
//		freelancer2.setPortfolios(portfolios);

        FreelancerBalance freelancerBalance2 = freelancer2.getBalance();
        freelancerBalance2.setAvailable(20000.0);
        freelancerBalance2.setWorkInProgress(4000.0);

        freelancerRepository.save(freelancer2);

        freelancer3.setSkills(new HashSet<>(skills3));
        freelancer3.setPricePerHour(70D);
        freelancer3.setRate(2);
        freelancer3.setDescription("best freelancer you will deal with ;)");
        freelancer3.setTitle("fullstack developer");
        freelancer3.setCountry("cairo,Egypt");
        freelancer3.setName("lara jreige");
        freelancer3.setProfilePicture("https://res.cloudinary.com/dhfb7i5h1/image/upload/v1740614562/freelancers_profile_pictures/ugwy7jflz41djmpmn1co.jpg");
        freelancer3.setLanguages(new HashSet<>(List.of("english")));
//		freelancer3.setPortfolios(portfolios);

        FreelancerBalance freelancerBalance3 = freelancer3.getBalance();
        freelancerBalance3.setAvailable(30000.0);
        freelancerBalance3.setWorkInProgress(6000.0);

        freelancerRepository.save(freelancer3);

    }



    public void freelancerWorkdoneWithPaymentseed() {
        Freelancer freelancer = freelancerRepository.findFreelancerById(userRepository.findByUsername("freelancer01").orElseThrow().getId()).orElseThrow();
        Client client = clientRepository.findByUser(userRepository.findByUsername("client01").orElseThrow()).orElseThrow();
        System.out.println("freelancer01 UUID: "+freelancer.getId());

        Job job = Job.builder()
                .title("healthcare website development")
                .client(client)
                .experienceLevel(ExperienceLevel.intermediate)
                .projectLength(ProjectLength._3_to_6_months)
                .status(Job.JobStatus.DONE)
                .description("this is the first job")
                .pricePerHour(40)
                .endedAt(new Date(2024-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
                .assignedTo(freelancer.getWorkerEntity())
                .build();

//		List<DeliverableFile> filesList = List.of(
//				DeliverableFile.builder()
//						.fileName("My Calendar")
//						.filePath()
//						.build()
//		);
//
//		List<DeliverableLink> linksList = List.of(
//				DeliverableLink.builder()
//						.fileName("Google")
//						.linkUrl("https://www.google.com/")
//						.build()
//		);

        List<Milestone> milestones = List.of(
                Milestone.builder()
                        .name("Contract1 - mile1")
                        .number(1)
//						.deliverableFiles(filesList)
//						.deliverableLinks(linksList)
                        .description("Mile1Desc")
                        .estimatedHours(5)
                        .dueDate( new Date(2026-1900, 1, 20, 15, 30, 0))
                        .status(Milestone.MilestoneStatus.APPROVED)
                        .build(),

                Milestone.builder()
                        .name("Contract1 - mile2")
                        .number(2)
                        .description("Mile2Desc")
                        .dueDate(new Date(2027-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
                        .estimatedHours(3)
                        .status(Milestone.MilestoneStatus.APPROVED)
                        .build(),
                Milestone.builder()
                        .name("Contract1 - mile2")
                        .number(3)
//						.deliverableFiles(filesList)
//						.deliverableLinks(linksList)
                        .description("Mile3Desc")
                        .estimatedHours(5)
                        .dueDate( new Date(2026-1900, 1, 20, 15, 30, 0))
                        .status(Milestone.MilestoneStatus.APPROVED)
                        .build()

        );

        Contract contract = Contract.builder()
                .job(job)
                .client(client)
                .startDate(new Date(2024-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
                .status(Contract.ContractStatus.ENDED)
                .milestones(milestones)
                .endDate(new Date())
                .workerEntity(freelancer.getWorkerEntity())
                .costPerHour(55.55)
                .payment(PaymentMethod.PerMilestones)
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
                .payment(PaymentMethod.PerMilestones)
                .workerEntity(freelancer.getWorkerEntity())
                .coverLetter("please accept me")
                .build();

        Payment payment = Payment.builder()
                .amount(1000D)
                .date(new Date())
                .sender(contract.getClient().getUser())
                .receiver(freelancer.getUser())
                .contract(contract)
                .type(Payment.Type.TRANSACTION)
                .build();

        paymentRepository.save(payment);
        jobRepository.save(job);
        proposalRepository.save(proposal1);
        freelancerRepository.save(freelancer);
        contractRepository.save(contract);

//		System.out.println("Contract 1 ID: "+contract.getId());
//		System.out.println("Community Contract 1, milestone 1 ID: "+contract.getMilestones().get(0).getId());


        Job job2 = Job.builder()
                .title("club website development")
                .client(client)
                .experienceLevel(ExperienceLevel.intermediate)
                .projectLength(ProjectLength._3_to_6_months)
                .status(Job.JobStatus.DONE)
                .description("Job Description:\n" +
                        "\n" +
                        "We are looking for an experienced Full-Stack Developer to join our team and help build a cutting-edge healthcare platform. This platform will serve as an integrated solution for managing patient information, appointments, prescriptions, and healthcare services.\n" +
                        "\n" +
                        "Responsibilities:\n" +
                        "Design, develop, and maintain both front-end and back-end systems for the healthcare platform.\n" +
                        "Build intuitive, user-friendly, and responsive user interfaces using modern web technologies.\n" +
                        "Develop and manage RESTful APIs to support the platform’s functionality.\n" +
                        "Integrate third-party services, such as payment gateways, email systems, and healthcare data APIs.\n" +
                        "Work closely with UI/UX designers to ensure the platform is both functional and aesthetically appealing.\n" +
                        "Implement security and privacy measures to ensure the platform complies with data protection regulations (e.g., HIPAA, GDPR).\n" +
                        "Conduct code reviews, write tests, and troubleshoot performance bottlenecks.\n" +
                        "Required Skills & Qualifications:\n" +
                        "Proven experience in full-stack development (3+ years).\n" )
                .pricePerHour(40)
                .endedAt(new Date(2025-1900, 1, 20, 15, 30, 0))
                .assignedTo(freelancer.getWorkerEntity())
                .build();


        List<Milestone> milestones2 = List.of(
                Milestone.builder()
                        .name("Contract2 - mile1")
                        .number(1)
                        .description("Mile1Desc")
                        .estimatedHours(5)
                        .dueDate(new Date(2025-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
                        .status(Milestone.MilestoneStatus.APPROVED)
                        .build(),

                Milestone.builder()
                        .name("Contract2 - mile2")
                        .number(2)
                        .description("Mile2Desc")
                        .estimatedHours(3)
                        .dueDate(new Date(2027-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
                        .status(Milestone.MilestoneStatus.APPROVED)
                        .build()
        );

        Contract contract2 = Contract.builder()
                .job(job2)
                .client(client)
                .status(Contract.ContractStatus.ENDED)
                .startDate(new Date(2024-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
                .milestones(milestones2)
                .workerEntity(freelancer.getWorkerEntity())
                .endDate(new Date())
                .payment(PaymentMethod.PerProject)
                .costPerHour(55.55)
                .build();
        job2.setContract(contract2);

        Proposal proposal2= Proposal.builder()
                .costPerHour(30D)
                .date(new Date())
                .milestones(milestones)
                .contract(contract2)
                .client(client)
                .status(Proposal.ProposalStatus.HIRED)
                .job(job2)
                .payment(PaymentMethod.PerProject)
                .workerEntity(freelancer.getWorkerEntity())
                .coverLetter("please accept me")
                .build();

        Payment payment2 = Payment.builder()
                .amount(444.4D)
                .date(new Date())
                .sender(contract2.getClient().getUser())
                .receiver(freelancer.getUser())
                .contract(contract2)
                .type(Payment.Type.TRANSACTION)
                .build();

        freelancerRepository.save(freelancer);
        jobRepository.save(job2);
        paymentRepository.save(payment2);
        proposalRepository.save(proposal2);
        contractRepository.save(contract2);

    }

    public void freelancerWorkInProgressSeed() {
        Freelancer freelancer = freelancerRepository.findFreelancerById(userRepository.findByUsername("freelancer01").orElseThrow().getId()).orElseThrow();
        Client client = clientRepository.findByUser(userRepository.findByUsername("client01").orElseThrow()).orElseThrow();

        Job activeJob = jobRepository.findByTitle("network engineer").get();
        activeJob.setStatus(Job.JobStatus.IN_PROGRESS);
        activeJob.setAssignedTo(freelancer.getWorkerEntity());
        jobRepository.save(activeJob);

        //create the milestones for the active job
        Milestone milestone = Milestone.builder()
                .name("5G Network Engineer - Milestone 1")
                .number(1)
                .description("Mile1Desc")
                .estimatedHours(5)
                .dueDate(new Date(2026-1900, 1, 20, 15, 30, 0))
                .status(Milestone.MilestoneStatus.IN_PROGRESS)
                .deliverableFiles(List.of(DeliverableFile.builder()
                        .fileName("first submission")
                        .filePath(Constants.DELIVERABLE_FILE_PATH)
                        .build()))
                .build();
        Milestone milestone2 = Milestone.builder()
                .name("5G Network Engineer - Milestone 2")
                .number(2)
                .description("Mile2Desc")
                .estimatedHours(3)
                .dueDate(new Date(2027-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
                .status(Milestone.MilestoneStatus.NOT_STARTED)
                .build();

        List<Milestone> milestones = List.of(milestone, milestone2);

        Contract activeContract = Contract.builder()
                .job(activeJob)
                .client(client)
                .status(Contract.ContractStatus.ACTIVE)
                .startDate(new Date(2024-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
                .milestones(milestones)
                .workerEntity(freelancer.getWorkerEntity())
                .endDate(new Date())
                .payment(PaymentMethod.PerProject)
                .costPerHour(55.559)
                .build();
        activeJob.setContract(activeContract);


        Contract deletedContract = Contract.builder()
                .job(activeJob)
                .client(client)
                .status(Contract.ContractStatus.REJECTED)
                .startDate(new Date(2024-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
                .milestones(milestones)
                .workerEntity(freelancer.getWorkerEntity())
                .endDate(new Date())
                .payment(PaymentMethod.PerProject)
                .costPerHour(30.0)
                .build();

        contractRepository.save(activeContract) ;
        contractRepository.save(deletedContract);

    }
}
