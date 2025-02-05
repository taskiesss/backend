package taskaya.backend.DTO.mappers;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.search.freelancers.FreelancerSearchResponseDTO;
import taskaya.backend.entity.freelancer.Freelancer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class FreelancerSearchResponseMapper {

    public FreelancerSearchResponseDTO toDTO(Freelancer freelancer){
        return FreelancerSearchResponseDTO.builder()
                .id(freelancer.getId())
                .name(freelancer.getUser().getUsername())
                .title(freelancer.getTitle())
                .description(freelancer.getDescription())
                .skills(freelancer.getSkills().stream().toList())
                .rate(freelancer.getFreelancerBusiness().getRate())
                .hourlySalary(freelancer.getFreelancerBusiness().getPricePerHour())
                .build();
    }

    public List<FreelancerSearchResponseDTO> toDTOList(List<Freelancer> freelancers){

        List<FreelancerSearchResponseDTO> result = new LinkedList<>();
        for (Freelancer freelancer :freelancers){
            result.add(toDTO(freelancer));
        }
        return result;
    }

    public Page<FreelancerSearchResponseDTO> toDTOPage(Page<Freelancer> freelancers){
        return new PageImpl<>(toDTOList(freelancers.getContent()), freelancers.getPageable(), freelancers.getTotalElements());
    }

}
