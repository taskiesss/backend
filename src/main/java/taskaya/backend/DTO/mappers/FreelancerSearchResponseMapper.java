package taskaya.backend.DTO.mappers;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.freelancers.responses.FreelancerSearchResponseDTO;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.freelancer.Freelancer;

import java.util.LinkedList;
import java.util.List;

@Component
public class FreelancerSearchResponseMapper {

    public static FreelancerSearchResponseDTO toDTO(Freelancer freelancer){
        String nameOrUsername = (freelancer.getName()==null || freelancer.getName().isEmpty() )? freelancer.getUser().getUsername() : freelancer.getName();
        return FreelancerSearchResponseDTO.builder()
                .id(freelancer.getId())
                .name(nameOrUsername)
                .title(freelancer.getTitle())
                .description((freelancer.getDescription()!= null &&freelancer.getDescription().length()>256)? freelancer.getDescription().substring(0,256)+"..." : freelancer.getDescription())
                .skills(freelancer.getSkills().stream().map(Skill::getName).toList())
                .rate(freelancer.getRate())
                .experienceLevel(freelancer.getExperienceLevel())
                .pricePerHour(freelancer.getPricePerHour())
                .profilePicture(freelancer.getProfilePicture())
                .build();
    }

    public static List<FreelancerSearchResponseDTO> toDTOList(List<Freelancer> freelancers){

        List<FreelancerSearchResponseDTO> result = new LinkedList<>();
        for (Freelancer freelancer :freelancers){
            result.add(toDTO(freelancer));
        }
        return result;
    }

    public static Page<FreelancerSearchResponseDTO> toDTOPage(Page<Freelancer> freelancers){
        return new PageImpl<>(toDTOList(freelancers.getContent()), freelancers.getPageable(), freelancers.getTotalElements());
    }

}
