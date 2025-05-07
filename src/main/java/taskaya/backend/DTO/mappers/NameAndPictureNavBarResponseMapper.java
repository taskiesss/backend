package taskaya.backend.DTO.mappers;

import taskaya.backend.DTO.login.NameAndPictureNavBarResponseDTO;
import taskaya.backend.DTO.login.NameAndPictureResponseDTO;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.freelancer.Freelancer;

public class NameAndPictureNavBarResponseMapper {
    public static NameAndPictureNavBarResponseDTO toDTO(Freelancer freelancer ,Integer newNotifications){
        return NameAndPictureNavBarResponseDTO.builder()
                .profilePicture(freelancer.getProfilePicture())
                .name(freelancer.getName()!=null?freelancer.getName():freelancer.getUser().getUsername())
                .role(User.Role.FREELANCER)
                .id(freelancer.getId().toString())
                .newNotifications(newNotifications)
                .build();
    }
    public static NameAndPictureNavBarResponseDTO toDTO(Client client, Integer newNotifications){
        return NameAndPictureNavBarResponseDTO.builder()
                .profilePicture(client.getProfilePicture())
                .name(client.getName()!=null? client.getName() : client.getUser().getUsername())
                .role(User.Role.CLIENT)
                .id(client.getId().toString())
                .newNotifications(newNotifications)
                .build();
    }
}
