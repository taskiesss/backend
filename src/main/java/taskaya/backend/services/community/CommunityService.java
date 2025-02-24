package taskaya.backend.services.community;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.communities.responses.CommunityProfileResponseDTO;
import taskaya.backend.DTO.workerEntity.responses.WorkerEntityWorkdoneResponseDTO;
import taskaya.backend.DTO.mappers.CommunityProfileResponseMapper;
import taskaya.backend.DTO.mappers.CommunitySearchResponseMapper;
import taskaya.backend.DTO.communities.requests.CommunitySearchRequestDTO;
import taskaya.backend.DTO.communities.responses.CommunitySearchResponseDTO;
import taskaya.backend.DTO.mappers.FreelancerWorkdoneResponseMapper;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.User;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.exceptions.notFound.NotFoundException;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.community.CommunityMemberRepository;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.work.JobRepository;
import taskaya.backend.specifications.CommunitySpecification;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class CommunityService {
    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    CommunityMemberRepository communityMemberRepository;

    public Community getCommunityByName(String communityName){
        return communityRepository.findByCommunityName(communityName)
                .orElseThrow(() -> new RuntimeException("Community not found with name: " + communityName));
    }

    public CommunitySearchResponseDTO searchCommunityByName(String communityName){
        return CommunitySearchResponseMapper.toDTO(communityRepository.findByCommunityName(communityName)
                .orElseThrow(() -> new RuntimeException("Community not found with name: " + communityName)));
    }


    @Transactional
    public void save(Community community){
        communityRepository.save(community);
    }

    public Page<CommunitySearchResponseDTO> searchCommunities(CommunitySearchRequestDTO requestDTO){
        Specification<Community> specCommunity = CommunitySpecification.searchCommunities(
                requestDTO.getSearch(),
                requestDTO.getSkills(),
                requestDTO.getExperienceLevel(),
                requestDTO.getHourlyRateMin(),
                requestDTO.getHourlyRateMax(),
                requestDTO.getRate(),
                requestDTO.getIsFull()
        );

        Pageable pageable;

        if (requestDTO.getSortBy() != null) {

            Sort sort;
            if (SortDirection.DESC.equals(requestDTO.getSortDirection())) {
                sort = Sort.by(Sort.Order.desc(requestDTO.getSortBy().getValue()));
            } else {
                sort = Sort.by(Sort.Order.asc(requestDTO.getSortBy().getValue()));
            }

            // Create Page Request for pagination
            pageable = PageRequest.of(requestDTO.getPage(), requestDTO.getSize(), sort);
        }else {
            pageable=PageRequest.of(requestDTO.getPage(), requestDTO.getSize());
        }

        // Get page of communities using specification, sorting and pagination
        Page<Community> communityPage = communityRepository.findAll(specCommunity, pageable);

        return CommunitySearchResponseMapper.toDTOPage(communityPage);
    }


    public CommunityProfileResponseDTO getCommunityProfile(String communityId){
        Community community = communityRepository.findById(UUID.fromString(communityId)).get();
//                .orElseThrow(()-> new NotFoundException("Community Not Found!"));

        CommunityProfileResponseDTO responseDTO = CommunityProfileResponseMapper.toDTO(community);

        User user = getUserFromJWT();
        boolean isAdmin = communityRepository.isAdmin(UUID.fromString(communityId), user.getId());
        if(isAdmin){
            responseDTO.setIsAdmin(true);
            responseDTO.setIsMember(true);
        }else{
            responseDTO.setIsAdmin(false);
            responseDTO.setIsMember(communityMemberRepository.isMember(UUID.fromString(communityId), user.getId()));
        }
        return responseDTO;
    }

    public Page<WorkerEntityWorkdoneResponseDTO> getCommunityWorkdone(String id, int page, int size){
        List<WorkerEntityWorkdoneResponseDTO> listDTO = new ArrayList<>();

        //get community
        Community community = communityRepository.findById(UUID.fromString(id))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));

        //get worker entity, get all jobs with status = "DONE"
        WorkerEntity workerEntity = community.getWorkerEntity();
        List<Job> jobs = jobRepository.findByAssignedToAndStatus(workerEntity, Job.JobStatus.DONE);
        jobs.sort(Comparator.comparing(Job::getEndedAt).reversed());

        //map to DTO list
        listDTO = FreelancerWorkdoneResponseMapper.toDTOList(jobs);

        //List to Page
        Pageable pageable = PageRequest.of(page, size);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), listDTO.size());

        List<WorkerEntityWorkdoneResponseDTO> paginatedList = listDTO.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, listDTO.size());
    }

    public User getUserFromJWT(){
        String username = JwtService.getAuthenticatedUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(()->new NotFoundException("Username not found!"));
    }
}
