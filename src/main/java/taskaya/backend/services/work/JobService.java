package taskaya.backend.services.work;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.clients.SimpleJobClientResponseDTO;
import taskaya.backend.DTO.jobs.requests.JobPostingDTO;
import taskaya.backend.DTO.mappers.JobDetailsResponseMapper;
import taskaya.backend.DTO.mappers.JobSearchResponseMapper;
import taskaya.backend.DTO.jobs.requests.JobSearchRequestDTO;
import taskaya.backend.DTO.jobs.responses.JobSearchResponseDTO;
import taskaya.backend.DTO.mappers.SimpleJobClientResponseMapper;
import taskaya.backend.DTO.proposals.responses.JobDetailsResponseDTO;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.Proposal;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.exceptions.login.FirstTimeFreelancerFormException;
import taskaya.backend.exceptions.notFound.NotFoundException;
import taskaya.backend.repository.SkillRepository;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.work.JobRepository;
import taskaya.backend.repository.work.ProposalRepository;
import taskaya.backend.services.client.ClientService;
import taskaya.backend.services.freelancer.FreelancerService;
import taskaya.backend.specifications.JobSpecification;

import java.util.*;


@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private FreelancerService freelancerService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private SkillRepository skillRepository;

    public Page<JobSearchResponseDTO> searchJobs(JobSearchRequestDTO request) {
        Specification<Job> spec = JobSpecification.filterJobs(request);

        Pageable pageable;

        if (request.getSortBy() != null) {
            String sortField = request.getSortBy().getValue().equals("rate")
                    ? "client.rate"
                    : request.getSortBy().getValue();

            Sort sort;
            if (SortDirection.DESC.equals(request.getSortDirection())) {

                sort = Sort.by(Sort.Order.desc(sortField));
            } else {
                sort = Sort.by(Sort.Order.asc(sortField));
            }

            // Create Page Request for pagination
            pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        } else {
            pageable = PageRequest.of(request.getPage(), request.getSize());
        }


        Page<Job> jobPage = jobRepository.findAll(spec, pageable);

        return JobSearchResponseMapper.toDTOPage(jobPage);
    }

    public Job findById(UUID uuid){
        return jobRepository.findById(uuid)
                .orElseThrow(() ->new RuntimeException("Job Not Found!"));
    }

    
    public JobDetailsResponseDTO getJobDetails (String jobId){
        Job job = jobRepository.findById(UUID.fromString(jobId))
                .orElseThrow(()->new NotFoundException("Job not found."));
        Client client = job.getClient();

        SimpleJobClientResponseDTO jobClientResponseDTO = SimpleJobClientResponseMapper.toDTO(client);
        JobDetailsResponseDTO responseDTO= JobDetailsResponseMapper.toDTO(job,jobClientResponseDTO);
        responseDTO.setCanApply(freelancerCanApplyanApply(job));
        return responseDTO;
    }
    
    public boolean freelancerCanApplyanApply(Job job){

        if(job.getAssignedTo() != null)
            return false;

        Freelancer freelancer;
        try {
             freelancer  = freelancerService.getFreelancerFromJWT();
        } catch (NotFoundException e) {
            return false;
        }

        // contains the worker entity of the freelancer + the worker entities of the communities which he is its leader
        List <WorkerEntity> freelancerWorkerEntities = new java.util.ArrayList<>(List.of(freelancer.getWorkerEntity()));
        freelancerWorkerEntities.addAll(
                communityRepository.findAllByAdmin(freelancer).stream().map(Community::getWorkerEntity).toList()
        );

        List proposalsList = proposalRepository.findByWorkerEntityInAndStatusAndJob(freelancerWorkerEntities, Proposal.ProposalStatus.PENDING,job);

        if (!proposalsList.isEmpty())
            return false;

        return true;

    }

    public void postJob(JobPostingDTO request) {


        Job job = Job.builder()
                .title(request.getTitle())
                .projectLength(request.getProjectLength())
                .experienceLevel(request.getExperienceLevel())
                .pricePerHour(request.getExpectedPricePerHour())
                .description(request.getDescription())
                .status(Job.JobStatus.NOT_ASSIGNED)
                .client(clientService.getClientFromJWT())
                .build();
        //this line is essential to prevent error in skills database ...(DK why)
        jobRepository.save(job);
        if (request.getSkills() == null
                || request.getSkills().isEmpty() ){
            throw new IllegalArgumentException("at least one skill should be selected");
        }else{
            Set<Skill> skills = new HashSet<>(request.getSkills());
            skillRepository.saveAll(skills);
            job.setSkills(skills);
        }

        jobRepository.save(job);
    }

    public void assignJobByContract(Contract contract){
        Job job = job = contract.getJob();
        job.setAssignedTo(contract.getWorkerEntity());
        job.setStatus(Job.JobStatus.IN_PROGRESS);
        job.setContract(contract);
        jobRepository.save(job);
    }
}