package taskaya.backend.repository.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.client.Client;

import java.util.UUID;
@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

}
