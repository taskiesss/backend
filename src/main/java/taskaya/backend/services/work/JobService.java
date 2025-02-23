package taskaya.backend.services.work;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.clients.SimpleJobClientResponseDTO;
import taskaya.backend.DTO.mappers.JobDetailsResponseMapper;
import taskaya.backend.DTO.mappers.JobSearchResponseMapper;
import taskaya.backend.DTO.jobs.requests.JobSearchRequestDTO;
import taskaya.backend.DTO.jobs.responses.JobSearchResponseDTO;
import taskaya.backend.DTO.mappers.SimpleJobClientResponseMapper;
import taskaya.backend.DTO.proposals.responses.JobDetailsResponseDTO;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.Proposal;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.exceptions.notFound.NotFoundException;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.work.JobRepository;
import taskaya.backend.repository.work.ProposalRepository;
import taskaya.backend.services.freelancer.FreelancerService;
import taskaya.backend.specifications.JobSpecification;

import java.util.List;
import java.util.UUID;


@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private FreelancerService freelancerService;
    @Autowired
    ProposalRepository proposalRepository;
    @Autowired
    CommunityRepository communityRepository;

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
}