package taskaya.backend.services.client;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.client.ClientBalance;
import taskaya.backend.entity.client.ClientBusiness;
import taskaya.backend.repository.client.ClientRepository;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Transactional
    public Client createClient(User user){
        user.setRole(User.Role.CLIENT);
        Client client = Client.builder()
                .id(user.getId())
                .user(user)
                .balance(new ClientBalance())
                .clientBusiness(new ClientBusiness())
                .build();

        clientRepository.save(client);
        return client;

    }


}
