package taskaya.backend.services.community;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import taskaya.backend.DTO.communities.communityMember.responses.CommunityMemberCommunityProfileDTO;
import taskaya.backend.DTO.communities.requests.*;
import taskaya.backend.DTO.communities.responses.*;
import taskaya.backend.DTO.freelancers.requests.DescriptionPatchRequestDTO;
import taskaya.backend.DTO.freelancers.requests.HeaderSectionUpdateRequestDTO;
import taskaya.backend.DTO.freelancers.requests.SkillsUpdateRequestDTO;
import taskaya.backend.DTO.mappers.*;
import taskaya.backend.DTO.workerEntity.responses.WorkerEntityWorkdoneResponseDTO;
import taskaya.backend.config.Constants;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.User;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.entity.community.JoinRequest;
import taskaya.backend.entity.community.Vote;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.freelancer.FreelancerBusiness;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.exceptions.notFound.NotFoundException;
import taskaya.backend.repository.SkillRepository;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.community.CommunityJoinRequestRepository;
import taskaya.backend.repository.community.CommunityMemberRepository;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.community.CommunityVoteRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.repository.work.ContractRepository;
import taskaya.backend.repository.work.JobRepository;
import taskaya.backend.services.CloudinaryService;
import taskaya.backend.services.MailService;
import taskaya.backend.services.NotificationService;
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

    @Autowired
    JwtService jwtService;

    @Autowired
    MailService mailService;

    @Autowired
    VoteService voteService;

    @Autowired
    SkillRepository skillRepository;

    @Autowired
    NotificationService notificationService;

    //di lazem ne3melaha autowire bel setter  MAHADESH YE8AIARHA
    private CommunityMemberService communityMemberService;

    @Autowired
    public void setCommunityMemberService(@Lazy CommunityMemberService communityMemberService) {
        this.communityMemberService = communityMemberService;
    }



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
    public void updateHeaderSection(String id, HeaderSectionUpdateRequestDTO requestDTO){

        if(requestDTO.getPricePerHour() == null
                ||requestDTO.getPricePerHour() < 0
                ||requestDTO.getAvgHoursPerWeek() == null
                ||requestDTO.getAvgHoursPerWeek() < 0
                ||requestDTO.getJobTitle() ==null
                ||requestDTO.getFirstName()==null)
            throw new RuntimeException("All fields are required");

        //get community
        Community community = communityRepository.findById(UUID.fromString(id))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));

        community.setCommunityName(requestDTO.getFirstName());
        community.setPricePerHour(requestDTO.getPricePerHour());
        community.getFreelancerBusiness().setAvgHoursPerWeek(requestDTO.getAvgHoursPerWeek());
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
    public void acceptToJoin(String communityId, AcceptToJoinRequestDTO request)throws MessagingException {
        //get community
        Community community = communityRepository.findById(UUID.fromString(communityId))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));

        if(request.getChoice().equals("accept")) {
            CommunityMember communityMember = communityMemberRepository
                    .findByCommunityAndPositionName(community, request.getPositionName())
                    .orElseThrow(() -> new RuntimeException("community member not found in community service"));

            Freelancer freelancer = freelancerRepository.findFreelancerById(UUID.fromString(request.getFreelancerId()))
                    .orElseThrow(() -> new RuntimeException("Freelancer not found!"));

            communityMember.setFreelancer(freelancer);

            List<Contract> pendingContracts = contractRepository.findAllByStatusAndWorkerEntity(Contract.ContractStatus.PENDING,community.getWorkerEntity());
            voteService.createVotesForNewMember(communityMember,pendingContracts);


            communityMemberService.saveMember(communityMember);
            communityJoinRequestRepository.deleteByPosition(communityMember);

            //send acceptance mail to freelancer
            notificationService.sendAcceptanceToFreelancer(community.getCommunityName(),freelancer.getUser(),communityId);
            mailService.sendAcceptanceToFreelanceAsync(freelancer.getUser().getEmail(), freelancer.getName(), community.getCommunityName());

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

        if (contract.getStatus() != Contract.ContractStatus.PENDING ) {
            throw new IllegalArgumentException("Contract is not in pending status");
        }

        Vote vote = communityVoteRepository.findByContractAndCommunityMember(contract,communityMember)
                .orElseThrow(()-> new NotFoundException("Vote Not Found!"));

        vote.setAgreed(request.getAgreed());
        communityVoteRepository.save(vote);
    }

    @Transactional
    public void updateIsFull(Community community) {

        for(CommunityMember communityMember :community.getCommunityMembers()){
            if (communityMember.getFreelancer()== null) {
                community.setIsFull(false);
                communityRepository.save(community);
                return;
            }

        }
        community.setIsFull(true);
        communityRepository.save(community);
    }

    public CommunityVotesDetailsResponseDTO getVotesDetails(String communityId, String contractId) {
        Community community = communityRepository.findById(UUID.fromString(communityId))
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));

        Contract contract = contractRepository.findById(UUID.fromString(contractId))
                .orElseThrow(()-> new NotFoundException("Contract Not Found in details!"));

        List<CommunityMemberCommunityProfileDTO> accepted=new ArrayList<>();
        List<CommunityMemberCommunityProfileDTO> rejected=new ArrayList<>();
        List<CommunityMemberCommunityProfileDTO> remaining=new ArrayList<>();

        for(CommunityMember communityMember : community.getCommunityMembers()){
            if(communityMember.getFreelancer()!=null){
                CommunityMemberCommunityProfileDTO communityMemberCommunityProfileDTO = CommunityMemberCommunityProfileResponseMapper.toDTO(communityMember);
                Vote vote = communityVoteRepository.findByContractAndCommunityMember(contract,communityMember)
                        .orElseThrow(()-> new NotFoundException("Vote Not Found!"));
                if(vote.getAgreed() == null){
                    remaining.add(communityMemberCommunityProfileDTO);
                }else if (vote.getAgreed().equals(false)) {
                    rejected.add(communityMemberCommunityProfileDTO);
                }else if (vote.getAgreed().equals(true)){
                    accepted.add(communityMemberCommunityProfileDTO);
                }
            }

        }
        return CommunityVotesDetailsResponseDTO.builder()
                .accepted(accepted)
                .rejected(rejected)
                .remaining(remaining)
                .build();
    }

    //helper functions :

    public boolean isCommunityAdmin(Freelancer freelancer, Community community) {
        return freelancer.getId().equals(community.getAdmin().getId());
    }

    public Boolean isUserCommunityAddmin(Community community){
        User user = jwtService.getUserFromToken();
        if (user.getRole() == User.Role.FREELANCER ) {
            Freelancer freelancer = freelancerRepository.findByUser(user)
                    .orElseThrow(()-> new NotFoundException("freelancer not found"));

            return isCommunityAdmin(freelancer,community);
        }
        return false;
    }

    public Page<CommunityAvailablePositionsResponseDTO> getAvailablePositions(String communityId, int page, int size) {
        Community community = communityRepository.findById(UUID.fromString(communityId))
                .orElseThrow(()-> new NotFoundException("Community not found"));

        Pageable pageable = PageRequest.of(page, size);

        Page<CommunityMember> communityMembers = communityMemberRepository.findByCommunityAndFreelancer(community,null,pageable);

        return CommunityAvailablePositionsResponseMapper.toDTOPage(communityMembers);
    }

    @Transactional
    public void requestToJoin(String communityId, long positionId) {
        Freelancer freelancer = freelancerService.getFreelancerFromJWT();
        Community community = communityRepository.findById(UUID.fromString(communityId))
                .orElseThrow(()-> new NotFoundException("Community not found"));
        CommunityMember position = communityMemberRepository.findByIdAndCommunity(positionId, community)
                .orElseThrow(()-> new NotFoundException("Community member not found"));

        communityJoinRequestRepository.deleteByFreelancerAndCommunity(freelancer,community);

        JoinRequest joinRequest = JoinRequest.builder()
                .position(position)
                .freelancer(freelancer)
                .community(community)
                .build();

        notificationService.joinRequestCommunityAdminNotification(community.getCommunityName(),position.getPositionName(),community.getAdmin().getUser(),communityId);
        communityJoinRequestRepository.save(joinRequest);
    }

    public Community getCommunityByWorkerEntity (WorkerEntity workerEntity){
        return communityRepository.findByWorkerEntity(workerEntity)
                .orElseThrow(()-> new NotFoundException("Community Not Found!"));
    }

    @Transactional
    public CreateCommunityResponseDTO createCommunity(CommunityCreateRequestDTO requestDTO) {
        if (requestDTO.getSkills()== null){
            requestDTO.setSkills(new LinkedList<>());
        }
        if (requestDTO.getCommunityPositions() == null){
            requestDTO.setCommunityPositions(new LinkedList<>());
        }
        if(requestDTO.getCommunityName()==null || requestDTO.getCommunityName().isEmpty()
                || requestDTO.getTitle()==null || requestDTO.getTitle().isEmpty()
                || requestDTO.getDescription()==null || requestDTO.getDescription().isEmpty()
                || requestDTO.getPricePerHour() == null || requestDTO.getPricePerHour() < 0
                || requestDTO.getAvrgHoursPerWeek() == null || requestDTO.getAvrgHoursPerWeek() < 0
                || requestDTO.getAdminRole() == null|| requestDTO.getAdminRole().getPositionName() == null || requestDTO.getAdminRole().getPositionName().isEmpty()
                || requestDTO.getAdminRole().getPercentage() == null || requestDTO.getAdminRole().getPercentage() < 0
        ){
            throw new RuntimeException("All fields are required");
        }

        List<Skill> skills = skillRepository.saveAll(requestDTO.getSkills());

        Community community = Community.builder()
                .title(requestDTO.getTitle())
                .communityName(requestDTO.getCommunityName())
                .admin(freelancerService.getFreelancerFromJWT())
                .pricePerHour(requestDTO.getPricePerHour())
                .description(requestDTO.getDescription())
                .skills(new HashSet<>(skills))
                .freelancerBusiness(FreelancerBusiness.builder().avgHoursPerWeek(requestDTO.getAvrgHoursPerWeek()).completedJobs(0).build())
                .isFull(requestDTO.getCommunityPositions().isEmpty()) //if empty ,only the admin then is full
                .profilePicture(Constants.COMMUNITY_FIRST_PROFILE_PICTURE)
                .coverPhoto(Constants.FIRST_COVER_PICTURE)
                .workerEntity(
                        WorkerEntity.builder()
                                .type(WorkerEntity.WorkerType.COMMUNITY)
                                .build()
                )
                .country("Cairo, Egypt")
                .build();

        community.getCommunityMembers().add(
                CommunityMember.builder()
                        .community(community)
                        .positionName(requestDTO.getAdminRole().getPositionName())
                        .positionPercent(requestDTO.getAdminRole().getPercentage())
                        .description(requestDTO.getAdminRole().getDescription())
                        .freelancer(community.getAdmin())
                        .build()
        );

        for (MemberForCreateCommunityDTO member : requestDTO.getCommunityPositions()) {
            if ( member.getPositionName()==null || member.getPositionName().isEmpty()
                    || member.getPercentage() == null || member.getPercentage() < 0) {
                throw new RuntimeException("percentage and position name are required");
            }
            community.getCommunityMembers().add(
                    CommunityMember.builder()
                            .community(community)
                            .positionName(member.getPositionName())
                            .positionPercent(member.getPercentage())
                            .description(member.getDescription())
                            .build()
            );
        }

        community.setStatus(Community.CommunityStatus.AVAILABLE);

        if (!communityMemberService.isTotalPercentagesValid(community.getCommunityMembers())){
            throw new RuntimeException("Total percentages must be equal to 100%");
        }
        communityRepository.save(community);


        return CreateCommunityResponseDTO.builder()
                .communityID(community.getUuid().toString()).build();


    }
}

