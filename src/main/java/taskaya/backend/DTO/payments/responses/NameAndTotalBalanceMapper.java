package taskaya.backend.DTO.payments.responses;

import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.freelancer.Freelancer;

public class NameAndTotalBalanceMapper {
    public static NameAndTotalBalanceResponseDTO toDTO(Freelancer freelancer){
        String name = freelancer.getName()==null?freelancer.getUser().getUsername():freelancer.getName();
        return NameAndTotalBalanceResponseDTO.builder()
                .name(name)
                .profilePicture(freelancer.getProfilePicture())
                .totalBalance(freelancer.getBalance().getAvailable())
                .build();
    }

    public static NameAndTotalBalanceResponseDTO toDTO(Client client){
        String name = client.getName()==null?client.getUser().getUsername():client.getName();
        return NameAndTotalBalanceResponseDTO.builder()
                .name(name)
                .profilePicture(client.getProfilePicture())
                .totalBalance(client.getBalance().getAvailable())
                .build();
    }
}
