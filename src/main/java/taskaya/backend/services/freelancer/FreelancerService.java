package taskaya.backend.services.freelancer;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import taskaya.backend.DTO.freelancers.requests.AvrHoursPerWeekUpdateRequestDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerOwnedCommunitiesResponseDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerWorkdoneResponseDTO;
import taskaya.backend.DTO.login.FirstTimeFreelancerFormDTO;
import taskaya.backend.DTO.mappers.FreelancerOwnedCommunitiesResponseMapper;
import taskaya.backend.DTO.mappers.FreelancerSearchResponseMapper;
import taskaya.backend.DTO.freelancers.responses.FreelancerSearchResponseDTO;
import taskaya.backend.DTO.freelancers.requests.FreenlancerSearchRequestDTO;
import taskaya.backend.DTO.mappers.FreelancerWorkdoneResponseMapper;
import taskaya.backend.config.Constants;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.User;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.freelancer.FreelancerBalance;
import taskaya.backend.entity.freelancer.FreelancerBusiness;
import taskaya.backend.entity.freelancer.FreelancerPortfolio;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.Milestone;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.exceptions.login.FirstTimeFreelancerFormException;
import taskaya.backend.repository.SkillRepository;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.repository.work.ContractRepository;
import taskaya.backend.repository.work.JobRepository;
import taskaya.backend.services.CloudinaryService;
import taskaya.backend.specifications.FreelancerSpecification;

import java.io.IOException;
import java.util.*;

@Service
public class FreelancerService {
    @Autowired
    FreelancerRepository freelancerRepository;

    @Autowired
    CommunityRepository communityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SkillRepository skillRepository;

    @Autowired
    private FreelancerSearchResponseMapper freelancerSearchResponseMapper;

    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    JobRepository jobRepository;


    @Transactional
    public Freelancer createFreelancer(User user){

        user.setRole(User.Role.FREELANCER);

        WorkerEntity workerEntity = WorkerEntity.builder()
                .type(WorkerEntity.WorkerType.FREELANCER)
                .build();

        Freelancer freelancer = Freelancer.builder()
                .id(user.getId())
                .user(user)
                .workerEntity(workerEntity)
                .balance(new FreelancerBalance())
                .profilePicture(Constants.FIRST_PROFILE_PICTURE)
                .freelancerBusiness(new FreelancerBusiness())
                .experienceLevel(ExperienceLevel.values()[new Random().nextInt(ExperienceLevel.values().length)])
                .pricePerHour(0.0)
                .build();
        freelancerRepository.save(freelancer);
        return freelancer;
    }



    public Page<FreelancerSearchResponseDTO> searchFreelancers(FreenlancerSearchRequestDTO requestDTO) {
        // Create Specification based on request
        Specification<Freelancer> specification = FreelancerSpecification.searchFreelancers(
                requestDTO.getSearch(),
                requestDTO.getSkills(),
                requestDTO.getExperienceLevel(),
                requestDTO.getHourlyRateMin(),
                requestDTO.getHourlyRateMax(),
                requestDTO.getRate()
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
        // Get page of freelancers based on specification, sorting, and pagination
        Page<Freelancer> freelancerPage = freelancerRepository.findAll(specification, pageable);

        // Convert to DTO Page
        return FreelancerSearchResponseMapper.toDTOPage(freelancerPage);
    }

    @Transactional
    public void saveFreelancer(Freelancer freelancer){
        freelancerRepository.save(freelancer);
    }

    public Freelancer getById(UUID uuid){
        return freelancerRepository.findFreelancerById(uuid)
                .orElseThrow(() -> new RuntimeException("Freelancer not found with ID: " + uuid));
    }

    @Transactional
    public void fillForm(FirstTimeFreelancerFormDTO firstTimeFreelancerFormDTO){
        String username = JwtService.getAuthenticatedUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Username not found!"));
        Freelancer freelancer = freelancerRepository.findByUser(user).get();


        //
        if(firstTimeFreelancerFormDTO.getFirstName() == null
                || firstTimeFreelancerFormDTO.getLastName() == null
                || firstTimeFreelancerFormDTO.getFirstName().isEmpty()
                || firstTimeFreelancerFormDTO.getLastName().isEmpty() ){
            throw new FirstTimeFreelancerFormException("First name and last name fields are required.");
        }else{
            String fullName = firstTimeFreelancerFormDTO.getFirstName() +" "+ firstTimeFreelancerFormDTO.getLastName();
            freelancer.setName(fullName);
        }

        if(firstTimeFreelancerFormDTO.getProfessionalTitle() == null
                || firstTimeFreelancerFormDTO.getProfessionalTitle().isEmpty()){
            throw new FirstTimeFreelancerFormException("Title is required.");
        }else {
            freelancer.setTitle(firstTimeFreelancerFormDTO.getProfessionalTitle());
        }

        if (firstTimeFreelancerFormDTO.getSkills() == null
                || firstTimeFreelancerFormDTO.getSkills().isEmpty() ){
            throw new FirstTimeFreelancerFormException("Skills are required.");
        }else{
            Set<Skill> skills = new HashSet<>(firstTimeFreelancerFormDTO.getSkills());
            skillRepository.saveAll(skills);
            freelancer.setSkills(skills);
        }

        if(firstTimeFreelancerFormDTO.getHourlyRate() == null
                || firstTimeFreelancerFormDTO.getHourlyRate() <=0){
            throw new FirstTimeFreelancerFormException("Hourly rate must be greater than 0.");
        }else{
            freelancer.setPricePerHour(firstTimeFreelancerFormDTO.getHourlyRate());
        }

        if(firstTimeFreelancerFormDTO.getProfessionalSummary() == null
                || firstTimeFreelancerFormDTO.getProfessionalSummary().isEmpty()
              ){
            throw new FirstTimeFreelancerFormException("Professional summary is required");
        }else if (firstTimeFreelancerFormDTO.getProfessionalSummary().length()> Constants.MAX_DESCRIPTION_SIZE){
            throw new FirstTimeFreelancerFormException("description length should not exceed 1000 chars");
        }
        else{
            freelancer.setDescription(firstTimeFreelancerFormDTO.getProfessionalSummary());
        }

        freelancer.getEducations().clear();  // Removes old educations (triggers orphan removal)
        freelancer.getEducations().addAll(firstTimeFreelancerFormDTO.getEducation());  // Adds new ones
        freelancer.setLanguages(new HashSet<>(firstTimeFreelancerFormDTO.getLanguages()));
        freelancer.getFreelancerBusiness().setAvgHoursPerWeek(firstTimeFreelancerFormDTO.getHoursPerWeek());

        freelancerRepository.save(freelancer);
    }

    public List<FreelancerOwnedCommunitiesResponseDTO> freelancerOwnedCommunities(){
        String username = JwtService.getAuthenticatedUsername();
        Freelancer freelancer = freelancerRepository.findByUser(userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("User not found!")))
                .orElseThrow(()->new RuntimeException("Username not found!"));
        List<FreelancerOwnedCommunitiesResponseDTO> responseDTOS = new ArrayList<>();
        responseDTOS.add(FreelancerOwnedCommunitiesResponseMapper.toDTO(freelancer));
        List<Community> communities = communityRepository.findAllByAdmin(freelancer);
        if(communities == null || communities.isEmpty()){
            System.out.println("This freelancer isn't an admin in any community");
        }else{
            for(Community community:communities){
                responseDTOS.add(FreelancerOwnedCommunitiesResponseMapper.toDTO(community));
            }
        }
        return responseDTOS;
    }

    @Transactional
    public void updateProfilePicture(MultipartFile updatedPicture) throws IOException {
        //get freelancer from token
        String username = JwtService.getAuthenticatedUsername();
        Freelancer freelancer = freelancerRepository.findByUser(userRepository.findByUsername(username)
                        .orElseThrow(()->new RuntimeException("User not found!")))
                .orElseThrow(()->new RuntimeException("Username not found!"));

        //delete old picture
        String currentPicture = freelancer.getProfilePicture();
        if(!(currentPicture.equals(Constants.FIRST_PROFILE_PICTURE)))
            cloudinaryService.deleteFile(currentPicture);

        //store new picture
        String pictureURL = cloudinaryService.uploadFile(updatedPicture, "profile_pictures");
        freelancer.setProfilePicture(pictureURL);
        freelancerRepository.save(freelancer);
    }


    public Page<FreelancerWorkdoneResponseDTO> getFreelancerWorkdone(String id, int page, int size){
        List<FreelancerWorkdoneResponseDTO> listDTO = new ArrayList<>();

        //get freelancer
        Freelancer freelancer = freelancerRepository.findFreelancerById(UUID.fromString(id))
                .orElseThrow(()->new RuntimeException("Freelancer Not Found!"));

        //get worker entity, get all jobs with status = "DONE"
        WorkerEntity workerEntity = freelancer.getWorkerEntity();
        List<Job> jobs = jobRepository.findByAssignedToAndStatus(workerEntity, Job.JobStatus.DONE);
        jobs.sort(Comparator.comparing(Job::getEndedAt).reversed());

        //map to DTO list
        listDTO = FreelancerWorkdoneResponseMapper.toDTOList(jobs);

        //List to Page
        Pageable pageable = PageRequest.of(page, size);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), listDTO.size());

        List<FreelancerWorkdoneResponseDTO> paginatedList = listDTO.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, listDTO.size());
    }

    @Transactional
    public void addPortfolio(String name, MultipartFile file) throws IOException{
        //get freelancer from token
        String username = JwtService.getAuthenticatedUsername();
        Freelancer freelancer = freelancerRepository.findByUser(userRepository.findByUsername(username)
                        .orElseThrow(()->new RuntimeException("User not found!")))
                .orElseThrow(()->new RuntimeException("Username not found!"));

        //upload and save changes in database
        String portfolioURL = cloudinaryService.uploadFile(file, "freelancer_portfolios");
        FreelancerPortfolio portfolio = FreelancerPortfolio.builder()
                .portfolioPdf(portfolioURL)
                .name(name)
                .build();
        List<FreelancerPortfolio> portfolioList = freelancer.getPortfolios();
        portfolioList.add(portfolio);
        freelancer.setPortfolios(portfolioList);
        freelancerRepository.save(freelancer);
    }

    @Transactional
    public void deletePortfolio(String filePath) throws IOException{
        //get freelancer from token
        String username = JwtService.getAuthenticatedUsername();
        Freelancer freelancer = freelancerRepository.findByUser(userRepository.findByUsername(username)
                        .orElseThrow(()->new RuntimeException("User not found!")))
                .orElseThrow(()->new RuntimeException("Username not found!"));

        boolean deleted = cloudinaryService.deleteFile(filePath);
        if(deleted){
            //if found and deleted in cloud, update database
            List<FreelancerPortfolio> portfolioList = freelancer.getPortfolios();
            portfolioList.removeIf(portfolio -> filePath.equals(portfolio.getPortfolioPdf()));
            freelancer.setPortfolios(portfolioList);
            freelancerRepository.save(freelancer);
        }
    }

    @Transactional
    public void updateAvrgHoursPerWeek(AvrHoursPerWeekUpdateRequestDTO requestDTO){
        //get freelancer from token
        String username = JwtService.getAuthenticatedUsername();
        Freelancer freelancer = freelancerRepository.findByUser(userRepository.findByUsername(username)
                        .orElseThrow(()->new RuntimeException("User not found!")))
                .orElseThrow(()->new RuntimeException("Username not found!"));

        freelancer.getFreelancerBusiness().setAvgHoursPerWeek(requestDTO.getAvrgHoursPerWeek());
        freelancerRepository.save(freelancer);
    }

}


