package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.clients.SimpleJobClientResponseDTO;
import taskaya.backend.entity.client.Client;

@Component
public class SimpleJobClientResponseMapper {
    public static SimpleJobClientResponseDTO toDTO(Client client){
        return SimpleJobClientResponseDTO.builder()
                .completedJobs(client.getClientBusiness().getCompletedJobs())
                .totalSpent(String.valueOf(client.getClientBusiness().getTotalSpent()))
                .rate((double)client.getRate())
                .build();
    }
}
