package taskaya.backend.services.community;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import taskaya.backend.DTO.communities.requests.AcceptToJoinRequestDTO;
import taskaya.backend.DTO.communities.requests.VoteRequestDTO;
import taskaya.backend.DTO.communities.responses.CommunityJoinReqResponseDTO;
import taskaya.backend.DTO.communities.responses.CommunityOfferResponseDTO;
import taskaya.backend.DTO.communities.responses.CommunityProfileResponseDTO;
import taskaya.backend.DTO.freelancers.requests.DescriptionPatchRequestDTO;
import taskaya.backend.DTO.freelancers.requests.HeaderSectionUpdateRequestDTO;
import taskaya.backend.DTO.freelancers.requests.SkillsUpdateRequestDTO;
import taskaya.backend.DTO.mappers.*;
import taskaya.backend.DTO.workerEntity.responses.WorkerEntityWorkdoneResponseDTO;
import taskaya.backend.DTO.communities.requests.CommunitySearchRequestDTO;
import taskaya.backend.DTO.communities.responses.CommunitySearchResponseDTO;
import taskaya.backend.config.Constants;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.User;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.entity.community.JoinRequest;
import taskaya.backend.entity.community.Vote;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.exceptions.notFound.NotFoundException;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.community.CommunityJoinRequestRepository;
import taskaya.backend.repository.community.CommunityMemberRepository;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.community.CommunityVoteRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.repository.work.ContractRepository;
import taskaya.backend.repository.work.JobRepository;
import taskaya.backend.services.CloudinaryService;
import taskaya.backend.services.freelancer.FreelancerService;
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

    @Autowired
    CommunityJoinRequestRepository communityJoinRequestRepository;

    @Autowired
    CommunityVoteRepository communityVoteRepository;

    @Autowired
    ContractRepository contractRepository;

    @Autowired
    FreelancerService freelancerService;

    @Autowired
    FreelancerRepository freelancerRepository;




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
        listDTO = WorkerEntityWorkdoneResponseMapper.toDTOList(jobs);

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
        if(!(currentPicture.equals(Constants.COMMUNITY_FIRST_PROFILE_PICTURE)))
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

    public Page<CommunityJoinReqResponseDTO> getJoinRequests(String communityId, int page, int size) {

        //get community
        Community community = communityRepository.findById(UUID.fromString(communityId))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));


        //List to Page
        Pageable pageable = PageRequest.of(page, size);

        Page<JoinRequest> joinRequests = communityJoinRequestRepository.findAllByCommunity(community,pageable);

        return CommunityJoinReqResponseMapper.toDTOPage(joinRequests);

    }


    public Page<CommunityOfferResponseDTO> getOffers(String communityId, int page, int size) {
        //get community
        Community community = communityRepository.findById(UUID.fromString(communityId))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));

        Pageable pageable = PageRequest.of(page, size);

        Page<Contract> contracts = contractRepository.findAllByStatusAndWorkerEntity(Contract.ContractStatus.PENDING,community.getWorkerEntity(),pageable);

        List<CommunityOfferResponseDTO> dtoList = new LinkedList<>();

        Freelancer freelancer = freelancerService.getFreelancerFromJWT();
        CommunityMember communityMember = communityMemberRepository.findByCommunityAndFreelancer(community,freelancer)
                .orElseThrow(()->new AccessDeniedException("you are not a member"));

        for(Contract contract : contracts.getContent()){
            Vote myVote = communityVoteRepository.findByContractAndCommunityMember(contract,communityMember)
                    .orElseThrow(()->new RuntimeException("runtime exception in community service"));

            List<Vote> votes = communityVoteRepository.findAllByContract(contract);
            CommunityOfferResponseDTO dto = CommunityOfferResponseMapper.toDTO(contract);

            dto.setAgreed(myVote.getAgreed());

            for (Vote vote : votes){
                if (vote.getAgreed()==null){
                    dto.setLeft(dto.getLeft()+1);
                } else if (vote.getAgreed().booleanValue()) {
                    dto.setVoted(dto.getVoted()+1);
                    dto.setAccepted(dto.getAccepted()+1);
                }else {
                    dto.setVoted(dto.getVoted()+1);
                    dto.setRejected(dto.getRejected()+1);
                }

            }
            dtoList.add(dto);
        }
        return new PageImpl<>(dtoList, contracts.getPageable(), contracts.getTotalElements());
    }

    @Transactional
    public void acceptToJoin(String communityId, AcceptToJoinRequestDTO request) {
        //get community
        Community community = communityRepository.findById(UUID.fromString(communityId))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));

        if(request.getChoice().equals("accept")) {
            CommunityMember communityMember = communityMemberRepository
                    .findByCommunityAndPositionName(community, request.getPositionName())
                    .orElseThrow(() -> new RuntimeException("community member not found in community service"));

            communityMember.setFreelancer(freelancerRepository.findFreelancerById(UUID.fromString(request.getFreelancerId()))
                    .orElseThrow(() -> new RuntimeException("Freelancer not found!")));

            communityJoinRequestRepository.deleteByPosition(communityMember);

        } else if (request.getChoice().equals("reject")) {
            CommunityMember communityMember = communityMemberRepository
                    .findByCommunityAndPositionName(community, request.getPositionName())
                    .orElseThrow(() -> new RuntimeException("community member not found in community service"));

            JoinRequest joinRequest =communityJoinRequestRepository.findByFreelancer(freelancerRepository
                    .findFreelancerById(UUID.fromString(request.getFreelancerId()))
                    .orElseThrow(() -> new RuntimeException("Freelancer not found in community service")));
            communityJoinRequestRepository.deleteById(joinRequest.getId());
        }else {
            throw new RuntimeException("Must accept or reject only");
        }

    }

    @Transactional
    public void vote(String communityId, VoteRequestDTO request) {
        Community community = communityRepository.findById(UUID.fromString(communityId))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));

        Freelancer freelancer = freelancerService.getFreelancerFromJWT();
        CommunityMember communityMember = communityMemberRepository.findByCommunityAndFreelancer(community,freelancer)
                .orElseThrow(()-> new NotFoundException("Community member Not Found!"));

        Contract contract =contractRepository.findById(UUID.fromString(request.getContractId()))
                .orElseThrow(()-> new NotFoundException("Contract Not Found!"));

        Vote vote = communityVoteRepository.findByContractAndCommunityMember(contract,communityMember)
                .orElseThrow(()-> new NotFoundException("Vote Not Found!"));

        vote.setAgreed(request.getAgreed());
        communityVoteRepository.save(vote);
    }
}
