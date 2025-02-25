package taskaya.backend.services.community;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import taskaya.backend.DTO.communities.responses.CommunityProfileResponseDTO;
import taskaya.backend.DTO.freelancers.requests.DescriptionPatchRequestDTO;
import taskaya.backend.DTO.freelancers.requests.HeaderSectionUpdateRequestDTO;
import taskaya.backend.DTO.freelancers.requests.SkillsUpdateRequestDTO;
import taskaya.backend.DTO.workerEntity.responses.WorkerEntityWorkdoneResponseDTO;
import taskaya.backend.DTO.mappers.CommunityProfileResponseMapper;
import taskaya.backend.DTO.mappers.CommunitySearchResponseMapper;
import taskaya.backend.DTO.communities.requests.CommunitySearchRequestDTO;
import taskaya.backend.DTO.communities.responses.CommunitySearchResponseDTO;
import taskaya.backend.DTO.mappers.FreelancerWorkdoneResponseMapper;
import taskaya.backend.config.Constants;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.User;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.exceptions.notFound.NotFoundException;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.community.CommunityMemberRepository;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.work.JobRepository;
import taskaya.backend.services.CloudinaryService;
import taskaya.backend.specifications.CommunitySpecification;

import java.io.IOException;
import java.util.*;

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

    @Autowired
    CloudinaryService cloudinaryService;

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
        Community community = communityRepository.findById(UUID.fromString(communityId))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));

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

    @Transactional
    @PreAuthorize("@jwtService.isCommunityAdmin(#id)")
    public void updateProfilePicture(String id, MultipartFile updatedPicture)throws IOException {
        //get community
        Community community = communityRepository.findById(UUID.fromString(id))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));

        List<String> allowedMimeTypes = List.of("image/jpeg", "image/png", "image/gif", "image/webp");
        // Validate file type
        if (updatedPicture.isEmpty() || !allowedMimeTypes.contains(updatedPicture.getContentType())) {
            throw new IllegalArgumentException("Invalid file type. Only JPEG, PNG, GIF, and WEBP images are allowed.");
        }

        //delete old picture
        String currentPicture = community.getProfilePicture();
        if(!(currentPicture.equals(Constants.FIRST_PROFILE_PICTURE)))
            cloudinaryService.deleteFile(currentPicture);

        //store new picture
        String pictureURL = cloudinaryService.uploadFile(updatedPicture, "communities_profile_pictures");
        community.setProfilePicture(pictureURL);
        communityRepository.save(community);
    }

    @Transactional
    @PreAuthorize("@jwtService.isCommunityAdmin(#id)")
    public void updateCoverPicture(String id, MultipartFile updatedPicture) throws IOException {
        //get community
        Community community = communityRepository.findById(UUID.fromString(id))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));

        List<String> allowedMimeTypes = List.of("image/jpeg", "image/png", "image/gif", "image/webp");
        // Validate file type
        if (updatedPicture.isEmpty() || !allowedMimeTypes.contains(updatedPicture.getContentType())) {
            throw new IllegalArgumentException("Invalid file type. Only JPEG, PNG, GIF, and WEBP images are allowed.");
        }

        //delete old picture
        String currentPicture = community.getCoverPhoto();
        if(!(currentPicture.equals(Constants.FIRST_COVER_PICTURE)))
            cloudinaryService.deleteFile(currentPicture);

        //store new picture
        String pictureURL = cloudinaryService.uploadFile(updatedPicture, "cover_photos");
        community.setCoverPhoto(pictureURL);
        communityRepository.save(community);
    }

    @Transactional
    @PreAuthorize("@jwtService.isCommunityAdmin(#id)")
    public void updateSkills(String id, SkillsUpdateRequestDTO skills){
        //get community
        Community community = communityRepository.findById(UUID.fromString(id))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));

        community.setSkills(new HashSet<>(skills.getSkills()));
        communityRepository.save(community);
    }

    @Transactional
    @PreAuthorize("@jwtService.isCommunityAdmin(#id)")
    public void updateDescription(String id, DescriptionPatchRequestDTO request) {
        //get community
        Community community = communityRepository.findById(UUID.fromString(id))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));
        community.setDescription(request.getDescription());
    }

    @Transactional
    @PreAuthorize("@jwtService.isCommunityAdmin(#id)")
    public void updateHeaderSection(String id, HeaderSectionUpdateRequestDTO requestDTO){

        if(requestDTO.getPricePerHour() == null
                ||requestDTO.getPricePerHour() < 0
                ||requestDTO.getCountry()==null
                ||requestDTO.getJobTitle() ==null
                ||requestDTO.getFirstName()==null)
            throw new RuntimeException("All fields are required");

        //get community
        Community community = communityRepository.findById(UUID.fromString(id))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));

        community.setCommunityName(requestDTO.getFirstName());
        community.setPricePerHour(requestDTO.getPricePerHour());
        community.setCountry(requestDTO.getCountry());
        community.setTitle(requestDTO.getJobTitle());

        communityRepository.save(community);
    }

    public User getUserFromJWT(){
        String username = JwtService.getAuthenticatedUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(()->new NotFoundException("Username not found!"));
    }
}
