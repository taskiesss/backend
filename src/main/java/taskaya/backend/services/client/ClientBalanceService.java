package taskaya.backend.services.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskaya.backend.entity.client.Client;
import taskaya.backend.repository.client.ClientBalanceRepository;

@Service
public class ClientBalanceService {
    @Autowired
    ClientBalanceRepository clientBalanceRepository;

    public void updateRestricted(Client client ,double amount){
        client.getBalance().setRestricted(client.getBalance().getRestricted() + amount);
        if (client.getBalance().getRestricted()<0){
            throw new RuntimeException("restricted amount cannot be negative");
        }
        clientBalanceRepository.save(client.getBalance());
    }
    public void updateAvailable(Client client ,double amount){
        client.getBalance().setAvailable(client.getBalance().getAvailable() + amount);
        if (client.getBalance().getAvailable()<0){
            throw new RuntimeException("available amount cannot be negative... no sufficient balance");
        }
        clientBalanceRepository.save(client.getBalance());
    }
}
