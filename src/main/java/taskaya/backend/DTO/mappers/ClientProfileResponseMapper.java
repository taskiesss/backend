package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.clients.ClientProfileResponseDTO;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.client.Client;

import java.util.stream.Collectors;

@Component
public class ClientProfileResponseMapper {
    public static ClientProfileResponseDTO toDTO(Client client) {
        return ClientProfileResponseDTO.builder()
                .uuid(client.getId().toString())
                .name(client.getName())
                .username(client.getUser().getUsername())
                .country(client.getCountry())
                .rate(client.getRate())
                .skills(client.getSkills().stream().map(Skill::toString).collect(Collectors.toList()))
                .languages(client.getLanguages().stream().toList())
                .description(client.getDescription())
                .profilePicture(client.getProfilePicture())
                .completedJobs(client.getClientBusiness().getCompletedJobs())
                .totalSpent(client.getClientBusiness().getTotalSpent())
                .build();
    }
}
