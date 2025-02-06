package taskaya.backend;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.enums.ProjectLength;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.entity.enums.ExperienceLevel;


import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.repository.SkillRepository;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.client.ClientRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;

import taskaya.backend.repository.work.JobRepository;
import taskaya.backend.repository.work.WorkerEntityRepository;
import taskaya.backend.services.client.ClientService;
import taskaya.backend.services.community.CommunityMemberService;
import taskaya.backend.services.community.CommunityService;
import taskaya.backend.services.freelancer.FreelancerService;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
	CommunityService communityService;

	@Autowired
	CommunityMemberService communityMemberService;

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		skillSeed();
		freelancerSeed();
		clientSeed();
		jobSeed();
		seedCommunityAndCommunityMember();
	}

	private void seedCommunityAndCommunityMember(){
		User user = User.builder()
				.username("minahany")
				.email("minahany@gmail.com")
				.password("minah@765")
				.role(User.Role.FREELANCER)
				.build();

		freelancerService.createFreelancer(user);

		List<String> mySkills = List.of("Java", "Spring Boot", "Spring Security", "Spring Data JPA", "Hibernate");
		List<Skill> skills = skillRepository.findByNameIn(mySkills);


		Community community = Community.builder()
				.communityName("FirstCommunity")
				.admin(freelancerService.getById(user.getId()))
				.workerEntity(freelancerService.getById(user.getId()).getWorkerEntity())
				.avrgHoursPerWeek(6)
				.pricePerHour(35)
				.status(Community.CommunityStatus.AVAILABLE)
				.rate(3)
				.skills(new HashSet<>(skills))
				.description("This is the first Comm")
				.isFull(false)
				.experienceLevel(ExperienceLevel.entry_level)
				.build();

		if (community.getCommunityMembers() == null) {
			community.setCommunityMembers(new ArrayList<>());
		}

		communityService.save(community);
		community = communityService.getCommunityByName("FirstCommunity");

		CommunityMember communityMember = CommunityMember.builder()
				.community(community)
				.positionName("firstAdmin")
				.positionPercent(4)
				.freelancer(freelancerService.getById(user.getId()))
				.build();

		communityMemberService.addMember(communityMember);
		communityMember = communityMemberService.findById(1);
		community.getCommunityMembers().add(communityMember);

		communityService.save(community);

		community = communityService.getCommunityByName("FirstCommunity");

	}

	private void jobSeed() {
		User user1 = userRepository.findByUsername("client01").get();
		Client client1 = clientRepository.findByUser(user1).get();

		Job job1 = Job.builder()
				.title("job1")
				.client(client1)
				.status(Job.JobStatus.NOT_ASSIGNED)
				.description("this is the first job")
				.expectedCostPerHour(40)
				.build();

		List<String> skillNames1 = List.of("Java", "Spring Boot", "Spring Security", "Spring Data JPA", "Hibernate");
		List<Skill> skills1 = skillRepository.findByNameIn(skillNames1);

		User user2 = userRepository.findByUsername("client02").get();
		Client client2 = clientRepository.findByUser(user2).get();  // Fixed line

		Job job2 = Job.builder()
				.title("job2")
				.client(client2)
				.status(Job.JobStatus.NOT_ASSIGNED)
				.description("this is the second job")
				.projectLength(ProjectLength._1_to_3_months)
				.expectedCostPerHour(20)
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

		freelancer1.setSkills(new HashSet<>(skills1));
		freelancerRepository.save(freelancer1);


		freelancer2.setSkills(new HashSet<>(skills2));
		freelancerRepository.save(freelancer2);

		freelancer3.setSkills(new HashSet<>(skills3));
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
				.email("2001480@gmail.com")
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
		clientRepository.save(client1);


		client2.setSkills(new HashSet<>(skills2));
		clientRepository.save(client2);

		client3.setSkills(new HashSet<>(skills3));
		clientRepository.save(client3);

	}


}



