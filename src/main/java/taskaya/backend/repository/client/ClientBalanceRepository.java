package taskaya.backend.repository.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.client.ClientBalance;
import taskaya.backend.entity.freelancer.FreelancerPortfolio;

@Repository
public interface ClientBalanceRepository extends JpaRepository<ClientBalance, Integer> {
}
