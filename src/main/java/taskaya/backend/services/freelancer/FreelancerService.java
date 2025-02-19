package taskaya.backend.services.freelancer;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.freelancers.requests.*;
import taskaya.backend.DTO.freelancers.responses.FreelancerOwnedCommunitiesResponseDTO;
import taskaya.backend.DTO.freelancers.responses.FreelancerProfileDTO;
import taskaya.backend.DTO.login.FirstTimeFreelancerFormDTO;
import taskaya.backend.DTO.mappers.FreelancerOwnedCommunitiesResponseMapper;
import taskaya.backend.DTO.mappers.FreelancerProfileMapper;
import taskaya.backend.DTO.mappers.FreelancerSearchResponseMapper;
import taskaya.backend.DTO.freelancers.responses.FreelancerSearchResponseDTO;
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
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.exceptions.notFound.NotFoundException;
import taskaya.backend.exceptions.login.FirstTimeFreelancerFormException;
import taskaya.backend.repository.SkillRepository;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.specifications.FreelancerSpecification;

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
                .experienceLevel(ExperienceLevel.entry_level)
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

        Freelancer freelancer = getFreelancerFromJWT();


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

        Freelancer freelancer = getFreelancerFromJWT();
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

    public FreelancerProfileDTO getProfileDetails(String id) {

        Freelancer freelancer ;

        if (id.equals("my_profile")){
            String myUsername = JwtService.getAuthenticatedUsername();
            User user = userRepository.findByUsername(myUsername).get();
            freelancer = freelancerRepository.findByUser(user)
                    .orElseThrow(()-> new NotFoundException("freelancer not found"));
        }else {
            freelancer = freelancerRepository.findById(UUID.fromString(id))
                    .orElseThrow(()->new NotFoundException("freelancer not found"));
        }

        return FreelancerProfileMapper.toDTO(freelancer);

    }

    @Transactional
    public void updateLanguages(LanguageDTO request) {
        Freelancer freelancer = getFreelancerFromJWT();
        freelancer.setLanguages(new HashSet<>(request.getLanguages()));
        freelancerRepository.save(freelancer);
    }

    @Transactional
    public void updateEducations(EducationsPatchRequestDTO request) {
        Freelancer freelancer = getFreelancerFromJWT();
        freelancer.getEducations().clear();
        freelancer.getEducations().addAll(request.getEducations());
        freelancerRepository.save(freelancer);
    }


    @Transactional
    public void updateLinkedIn(LinkedInPatchRequestDTO request) {
        Freelancer freelancer = getFreelancerFromJWT();
        freelancer.setLinkedIn(request.getLinkedIn());
    }

    @Transactional
    public void updateDesc(DescriptionPatchRequestDTO request) {
        Freelancer freelancer = getFreelancerFromJWT();
        freelancer.setDescription(request.getDescription());
    }

    public void updateEmpHistory(EmployeeHistoryPatchDTO request) {
        Freelancer freelancer = getFreelancerFromJWT();
        freelancer.getEmployeeHistories().clear();
        freelancer.getEmployeeHistories().addAll(request.getEmployeeHistory());
        freelancerRepository.save(freelancer);
    }






    Freelancer getFreelancerFromJWT(){
        String username = JwtService.getAuthenticatedUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Username not found!"));
        return freelancerRepository.findByUser(user).orElseThrow(()-> new NotFoundException("freelancer not found"));
    }



}


