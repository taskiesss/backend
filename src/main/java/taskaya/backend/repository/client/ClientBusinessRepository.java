package taskaya.backend.repository.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.client.ClientBusiness;

@Repository
public interface ClientBusinessRepository extends JpaRepository<ClientBusiness, Integer> {

}
