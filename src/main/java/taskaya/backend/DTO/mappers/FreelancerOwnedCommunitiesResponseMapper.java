package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.freelancers.responses.FreelancerOwnedCommunitiesResponseDTO;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.freelancer.Freelancer;

@Component
public class FreelancerOwnedCommunitiesResponseMapper {
    public static FreelancerOwnedCommunitiesResponseDTO toDTO(Freelancer freelancer){
        return FreelancerOwnedCommunitiesResponseDTO.builder()
                .id(freelancer.getWorkerEntity().getId().toString())
                .name(freelancer.getName())
                .build();
    }

    public static FreelancerOwnedCommunitiesResponseDTO toDTO(Community community){
        return FreelancerOwnedCommunitiesResponseDTO.builder()
                .id(community.getWorkerEntity().getId().toString())
                .name(community.getCommunityName())
                .build();
    }
}
