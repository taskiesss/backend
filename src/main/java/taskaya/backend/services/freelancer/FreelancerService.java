package taskaya.backend.services.freelancer;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.mappers.FreelancerSearchResponseMapper;
import taskaya.backend.DTO.search.freelancers.FreelancerSearchResponseDTO;
import taskaya.backend.DTO.search.freelancers.FreenlancerSearchRequestDTO;
import taskaya.backend.entity.User;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.freelancer.FreelancerBalance;
import taskaya.backend.entity.freelancer.FreelancerBusiness;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.specifications.FreelancerSpecification;

import java.util.Random;
import java.util.UUID;

@Service
public class FreelancerService {
    @Autowired
    FreelancerRepository freelancerRepository;

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

        Sort sort;
        if (SortDirection.DESC.equals(requestDTO.getSortDirection())) {
            sort = Sort.by(Sort.Order.desc(requestDTO.getSortBy().getValue()));
        } else {
            sort = Sort.by(Sort.Order.asc(requestDTO.getSortBy().getValue()));
        }

        // Create PageRequest for pagination
        Pageable pageable = PageRequest.of(requestDTO.getPage(), requestDTO.getSize(), sort);

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
}


