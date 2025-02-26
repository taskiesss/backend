package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.freelancers.responses.FreelancerOwnedCommunitiesResponseDTO;
import taskaya.backend.DTO.login.NameAndPictureResponseDTO;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.freelancer.Freelancer;

@Component
public class NameAndPictureResponseMapper {
    public static NameAndPictureResponseDTO toDTO(Freelancer freelancer){
        return NameAndPictureResponseDTO.builder()
                .profilePicture(freelancer.getProfilePicture())
                .name(freelancer.getUser().getUsername())
                .role(User.Role.FREELANCER)
                .build();
    }
    public static NameAndPictureResponseDTO toDTO(Client client){
        return NameAndPictureResponseDTO.builder()
                .profilePicture(client.getProfilePicture())
                .name(client.getUser().getUsername())
                .role(User.Role.CLIENT)
                .build();
    }
}
