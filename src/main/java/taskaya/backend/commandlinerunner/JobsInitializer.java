package taskaya.backend.commandlinerunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.enums.ProjectLength;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.repository.SkillRepository;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.client.ClientRepository;
import taskaya.backend.repository.work.JobRepository;

import java.util.HashSet;
import java.util.List;

@Component
public class JobsInitializer {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private JobRepository jobRepository;
    public void jobSeed() {
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


    }

}
