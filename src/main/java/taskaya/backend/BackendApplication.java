package taskaya.backend;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.milestones.requests.MilestoneSubmitProposalRequestDTO;
import taskaya.backend.DTO.proposals.requests.SubmitProposalRequestDTO;
import taskaya.backend.config.Constants;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.enums.Payment;
import taskaya.backend.entity.enums.ProjectLength;
import taskaya.backend.entity.freelancer.Freelancer;

import taskaya.backend.entity.freelancer.FreelancerPortfolio;

import taskaya.backend.entity.work.*;

import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.entity.enums.ExperienceLevel;


import taskaya.backend.repository.SkillRepository;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.client.ClientRepository;
import taskaya.backend.repository.community.CommunityRepository;
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

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}

@Component
 class MyCommandLineRunner implements CommandLineRunner {

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

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		skillSeed();
		freelancerSeed();
		clientSeed();
		jobSeed();
		communityWithAdmin();
		seedCommunityAndCommunityMember();
//		proposalSeed();
		freelancerWorkdoneseed();
		communityWorkdoneSeed();
	}

	private void communityWorkdoneSeed() {
		Community community = communityRepository.findByCommunityName("mina community").orElseThrow();
		Client client = clientRepository.findByUser(userRepository.findByUsername("client01").orElseThrow()).orElseThrow();
		System.out.println("Community UUID: "+community.getUuid());

		Job job = Job.builder()
				.title("JobWorkdone1")
				.client(client)
				.experienceLevel(ExperienceLevel.intermediate)
				.projectLength(ProjectLength._3_to_6_months)
				.status(Job.JobStatus.DONE)
				.description("this is the first job")
				.pricePerHour(40)
				.endedAt(new Date(2024-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
				.assignedTo(community.getWorkerEntity())
				.build();


		List<Milestone> milestones = List.of(
				Milestone.builder()
						.name("mile1")
						.number(1)
						.estimatedHours(5)
						.dueDate( new Date(2026-1900, 1, 20, 15, 30, 0))
						.status(Milestone.MilestoneStatus.APPROVED)
						.build(),

				Milestone.builder()
						.name("mile2")
						.number(2)
						.dueDate(new Date(2027-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
						.estimatedHours(3)
						.status(Milestone.MilestoneStatus.APPROVED)
						.build()
		);

		Contract contract = Contract.builder()
				.job(job)
				.client(client)
				.status(Contract.ContractStatus.ENDED)
				.milestones(milestones)
				.workerEntity(community.getWorkerEntity())
				.costPerHour(55.55)
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
				.payment(Payment.PerProject)
				.workerEntity(community.getWorkerEntity())
				.coverLetter("please accept me")
				.build();

		jobRepository.save(job);
		proposalRepository.save(proposal1);
		communityRepository.save(community);
		contractRepository.save(contract);


		Job job2 = Job.builder()
				.title("JobWorkdone2")
				.client(client)
				.experienceLevel(ExperienceLevel.intermediate)
				.projectLength(ProjectLength._3_to_6_months)
				.status(Job.JobStatus.DONE)
				.description("this is the sec job")
				.pricePerHour(40)
				.endedAt(new Date(2025-1900, 1, 20, 15, 30, 0))
				.assignedTo(community.getWorkerEntity())
				.build();


		List<Milestone> milestones2 = List.of(
				Milestone.builder()
						.name("mile1")
						.number(1)
						.estimatedHours(5)
						.dueDate(new Date(2025-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
						.status(Milestone.MilestoneStatus.APPROVED)
						.build(),

				Milestone.builder()
						.name("mile2")
						.number(2)
						.estimatedHours(3)
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
				.payment(Payment.PerProject)
				.workerEntity(community.getWorkerEntity())
				.coverLetter("please accept me")
				.build();

		communityRepository.save(community);
		jobRepository.save(job2);
		proposalRepository.save(proposal2);
		contractRepository.save(contract2);
	}

	private void freelancerWorkdoneseed() {
		Freelancer freelancer = freelancerRepository.findFreelancerById(userRepository.findByUsername("freelancer01").orElseThrow().getId()).orElseThrow();
		Client client = clientRepository.findByUser(userRepository.findByUsername("client01").orElseThrow()).orElseThrow();
		System.out.println("freelancer01 UUID: "+freelancer.getId());

		Job job = Job.builder()
				.title("JobWorkdone1")
				.client(client)
				.experienceLevel(ExperienceLevel.intermediate)
				.projectLength(ProjectLength._3_to_6_months)
				.status(Job.JobStatus.DONE)
				.description("this is the first job")
				.pricePerHour(40)
				.endedAt(new Date(2024-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
				.assignedTo(freelancer.getWorkerEntity())
				.build();


		List<Milestone> milestones = List.of(
				Milestone.builder()
						.name("mile1")
						.number(1)
						.estimatedHours(5)
						.dueDate( new Date(2026-1900, 1, 20, 15, 30, 0))
						.status(Milestone.MilestoneStatus.APPROVED)
						.build(),

				Milestone.builder()
						.name("mile2")
						.number(2)
						.dueDate(new Date(2027-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
						.estimatedHours(3)
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
				.payment(Payment.PerProject)
				.workerEntity(freelancer.getWorkerEntity())
				.coverLetter("please accept me")
				.build();

		jobRepository.save(job);
		proposalRepository.save(proposal1);
		freelancerRepository.save(freelancer);
		contractRepository.save(contract);


		Job job2 = Job.builder()
				.title("JobWorkdone2")
				.client(client)
				.experienceLevel(ExperienceLevel.intermediate)
				.projectLength(ProjectLength._3_to_6_months)
				.status(Job.JobStatus.DONE)
				.description("this is the sec job")
				.pricePerHour(40)
				.endedAt(new Date(2025-1900, 1, 20, 15, 30, 0))
				.assignedTo(freelancer.getWorkerEntity())
				.build();


		List<Milestone> milestones2 = List.of(
				Milestone.builder()
						.name("mile1")
						.number(1)
						.estimatedHours(5)
						.dueDate(new Date(2025-1900, Calendar.FEBRUARY, 20, 15, 30, 0))
						.status(Milestone.MilestoneStatus.APPROVED)
						.build(),

				Milestone.builder()
						.name("mile2")
						.number(2)
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
				.payment(Payment.PerProject)
				.workerEntity(freelancer.getWorkerEntity())
				.coverLetter("please accept me")
				.build();

		freelancerRepository.save(freelancer);
		jobRepository.save(job2);
		proposalRepository.save(proposal2);
		contractRepository.save(contract2);
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
				.freelancerPayment(Payment.PerMilestones)
				.coverLetter("Please accept my proposal, I need money :_)")
				.milestones(milestoneList)
				.build();

		proposalService.createProposal(submitProposalRequestDTO,myJob.getUuid());

		WorkerEntity commWorkerEntity = communityService.getCommunityByName("mina Community").getWorkerEntity();

		SubmitProposalRequestDTO commRequestDTO = SubmitProposalRequestDTO.builder()
				.jobId(myJob.getUuid().toString())
				.candidateId(commWorkerEntity.getId().toString()) //workerEntity ID
				.pricePerHour(14.6)
				.freelancerPayment(Payment.PerProject)
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

		if (community.getCommunityMembers() == null) {
			community.setCommunityMembers(new ArrayList<>());
		}

		communityService.save(community);
		community = communityService.getCommunityByName(name+" Community");

		System.out.println("Mina Community ID: " + community.getUuid());

		CommunityMember communityMember = CommunityMember.builder()
				.community(community)
				.positionName("Admin")
				.positionPercent(4)
				.freelancer(freelancerService.getById(user.getId()))
				.build();


		communityMemberService.addMember(communityMember);
		communityMember = communityMemberService.findById(1);
		community.getCommunityMembers().add(communityMember);

		communityService.save(community);
	}


	public void communityWithAdmin(){
		User user = userRepository.findByUsername("freelancer01").get();
		//userRepository.save(user);
		List<String> mySkills = List.of("Java", "Spring Boot", "Spring Security", "Spring Data JPA", "Hibernate");
		List<Skill> skills = skillRepository.findByNameIn(mySkills);

		WorkerEntity workerEntity = WorkerEntity.builder()
				.type(WorkerEntity.WorkerType.COMMUNITY)
				.build();
		Community community = Community.builder()
				.communityName("Pablo"+" Community")
				.admin(freelancerRepository.findByUser(user).get())
				.workerEntity(workerEntity)
				.avrgHoursPerWeek(6)
				.pricePerHour(35)
				.status(Community.CommunityStatus.AVAILABLE)
				.rate(3)
				.skills(new HashSet<>(skills))
				.description("This is the sdhsbdcjksbvkjsbvdjsbdvj,sbdvj,sdvjkgsdfjhbdcjsdbcjksbdccj,sjsbcsj")
				.isFull(false)
				.experienceLevel(ExperienceLevel.expert)
				.build();

		communityService.save(community);
	}

	private void jobSeed() {
		User user1 = userRepository.findByUsername("client01").get();
		Client client1 = clientRepository.findByUser(user1).get();

		User user2 = userRepository.findByUsername("client02").get();
		Client client2 = clientRepository.findByUser(user2).get();

		User user3 = userRepository.findByUsername("client03").get();
		Client client3 = clientRepository.findByUser(user3).get();

		Job job1 = Job.builder()
				.title("job1")
				.client(client1)
				.experienceLevel(ExperienceLevel.intermediate)
				.projectLength(ProjectLength._3_to_6_months)
				.status(Job.JobStatus.NOT_ASSIGNED)
				.description("this is the first job")
				.pricePerHour(40)
				.build();

		List<String> skillNames1 = List.of("Java", "Spring Boot", "Spring Security", "Spring Data JPA", "Hibernate");
		List<Skill> skills1 = skillRepository.findByNameIn(skillNames1);

		List<Job> jobs = List.of(
				Job.builder()
						.title("Backend Developer")
						.client(client2)
						.experienceLevel(ExperienceLevel.entry_level)
						.projectLength(ProjectLength._1_to_3_months)
						.status(Job.JobStatus.NOT_ASSIGNED)
						.description("Looking for an experienced backend developer with Spring Boot expertise.")
						.pricePerHour(50)
						.build(),

				Job.builder()
						.title("Full-Stack Engineer")
						.client(client1)
						.experienceLevel(ExperienceLevel.entry_level)
						.projectLength(ProjectLength._less_than_1_month)
						.status(Job.JobStatus.NOT_ASSIGNED)
						.description("Need a skilled full-stack engineer to work on a freelancing platform.")
						.pricePerHour(45)
						.build(),

				Job.builder()
						.title("Microservices Specialist")
						.client(client1)
						.experienceLevel(ExperienceLevel.expert)
						.projectLength(ProjectLength._more_than_6_months)
						.status(Job.JobStatus.NOT_ASSIGNED)
						.description("Seeking a microservices expert for API development using Spring Boot and Kafka.")
						.pricePerHour(60)
						.build(),

				Job.builder()
						.title("Database Optimization Expert")
						.client(client1)
						.experienceLevel(ExperienceLevel.expert)
						.projectLength(ProjectLength._less_than_1_month)
						.status(Job.JobStatus.NOT_ASSIGNED)
						.description("Require a database expert to optimize queries and performance in PostgreSQL.")
						.pricePerHour(55)
						.build(),

				Job.builder()
						.title("Security Engineer")
						.client(client1)
						.experienceLevel(ExperienceLevel.intermediate)
						.projectLength(ProjectLength._more_than_6_months)
						.status(Job.JobStatus.NOT_ASSIGNED)
						.description("Looking for a security expert to audit and improve authentication using JWT.")
						.pricePerHour(65)
						.build()
		);

// Save jobs to the repository
		// Assign skills to each job

		jobs.forEach(job -> job.setSkills(new HashSet<>(skills1)));

		jobRepository.saveAll(jobs);

// Retrieve skills for these jobs
		List<String> skillNames = List.of("Machine Learning", "Deep Learning", "Natural Language Processing (NLP)");
		List<Skill> skills = skillRepository.findByNameIn(skillNames);



		List<Job> moreJobs = List.of(
				Job.builder()
						.title("DevOps Engineer")
						.client(client2)
						.status(Job.JobStatus.NOT_ASSIGNED)
						.description("Seeking a DevOps engineer to set up CI/CD pipelines and automate deployments.")
						.pricePerHour(70)
						.build(),

				Job.builder()
						.title("Cloud Architect")
						.client(client1)
						.status(Job.JobStatus.NOT_ASSIGNED)
						.description("Need an AWS/GCP cloud architect to design and deploy scalable cloud solutions.")
						.pricePerHour(75)
						.build(),

				Job.builder()
						.title("Machine Learning Engineer")
						.client(client3)
						.status(Job.JobStatus.NOT_ASSIGNED)
						.description("Looking for an ML engineer to build recommendation algorithms and data models.")
						.pricePerHour(80)
						.build(),

				Job.builder()
						.title("Mobile App Developer")
						.client(client3)
						.status(Job.JobStatus.NOT_ASSIGNED)
						.description("Need a mobile developer to create a cross-platform app using Flutter or React Native.")
						.pricePerHour(50)
						.build(),

				Job.builder()
						.title("API Developer")
						.client(client1)
						.status(Job.JobStatus.NOT_ASSIGNED)
						.description("Looking for an API developer to build RESTful services and integrate third-party APIs.")
						.pricePerHour(60)
						.build()
		);

// Save new jobs to the repository
		jobRepository.saveAll(moreJobs);

//		Job testJob = jobRepository.findByTitle("Backend Developer")
//				.orElseThrow(()->new RuntimeException("NotFound"));
//		System.out.println("Test Job ID: "+testJob.getUuid());

// Assign the same set of skills to the new jobs
		moreJobs.forEach(job -> job.setSkills(new HashSet<>(skills)));

// Save the updated jobs with assigned skills
		jobRepository.saveAll(moreJobs);



		Job job2 = Job.builder()
				.title("job2")
				.client(client2)
				.status(Job.JobStatus.NOT_ASSIGNED)
				.description("this is the second job")
				.experienceLevel(ExperienceLevel.intermediate)
				.projectLength(ProjectLength._3_to_6_months)
				.pricePerHour(20)
				.build();

		List<String> skillNames2 = List.of("AWS", "Google Cloud", "Azure");
		List<Skill> skills2 = skillRepository.findByNameIn(skillNames2);

		job1.setSkills(new HashSet<>(skills1));
		job2.setSkills(new HashSet<>(skills2));
		jobRepository.saveAll(List.of(job1, job2));

		WorkerEntity worker2 = WorkerEntity.builder()
				.type(WorkerEntity.WorkerType.FREELANCER)
				.build();

		workerEntityRepository.save(worker2);

		job2.setAssignedTo(worker2);
		System.out.println("---------------------job-----------------------");
		System.out.println(job2.getUuid());
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

				// 🗄️ Databases
				"SQL", "PostgreSQL", "MySQL", "MariaDB", "SQLite", "Oracle DB", "Microsoft SQL Server", "MongoDB",
				"Firebase", "Cassandra", "Neo4j", "DynamoDB", "Redis", "Elasticsearch", "InfluxDB", "Supabase",

				// ☁️ Cloud & DevOps
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

				// ✍️ Content Creation & Writing
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

				// 🛠️ Miscellaneous
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


	private void freelancerSeed(){

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
						.portfolioPdf("https://res.cloudinary.com/dhfb7i5h1/raw/upload/v1740527119/freelancer_portfolios/538180e5-9e56-4742-9138-1544796ed43e.pdf")
						.name("Portfolio01")
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
		freelancer2.setPortfolios(portfolios);
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
		freelancer3.setPortfolios(portfolios);
		freelancerRepository.save(freelancer3);





	}



	private void clientSeed() {

		User clientUser1 = User.builder()
				.email("2000966@eng.asu.edu.eg")
				.role(User.Role.CLIENT)
				.username("client01")
				.password(new BCryptPasswordEncoder().encode("client1@123"))
				.build();

		List<String> skillNames1 = List.of("Java", "Spring Boot", "Spring Security");
		List<Skill> skills1 = skillRepository.findByNameIn(skillNames1);

		User clientUser2 = User.builder()
				.email("2001480@eng.asu.edu.eg")
				.role(User.Role.CLIENT)
				.username("client02")
				.password(new BCryptPasswordEncoder().encode("client2@123"))
				.build();

		List<String> skillNames2 = List.of("AWS", "Google Cloud");
		List<Skill> skills2 = skillRepository.findByNameIn(skillNames2);

		User clientUser3 = User.builder()
				.email("2001024@gmail.com")
				.role(User.Role.CLIENT)
				.username("client03")
				.password(new BCryptPasswordEncoder().encode("client3@123"))
				.build();

		List<String> skillNames3 = List.of("Machine Learning", "Natural Language Processing (NLP)","kjhgfd");
		List<Skill> skills3 = skillRepository.findByNameIn(skillNames3);



		Client client1 = clientService.createClient(clientUser1);
		Client client2 = clientService.createClient(clientUser2);
		Client client3 = clientService.createClient(clientUser3);

		client1.setSkills(new HashSet<>(skills1));
		client1.setRate(3);
		clientRepository.save(client1);


		client2.setSkills(new HashSet<>(skills2));
		client2.setRate(2);
		clientRepository.save(client2);

		client3.setSkills(new HashSet<>(skills3));
		client3.setRate(3.5F);
		clientRepository.save(client3);

	}


}



