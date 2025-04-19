package taskaya.backend.services.freelancer;


import org.springframework.stereotype.Service;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.freelancer.FreelancerBusiness;
import taskaya.backend.repository.freelancer.FreelancerBalanceRepository;
import taskaya.backend.repository.freelancer.FreelancerBusinessRepository;

@Service
public class FreelancerBalanceService {
    FreelancerBalanceRepository freelancerBalanceRepository;

    public void updateFreelancerworkInProgress(Freelancer freelancer, Double amount) {

        freelancer.getBalance().setWorkInProgress(freelancer.getBalance().getWorkInProgress() + amount);
        if (freelancer.getBalance().getWorkInProgress()<0){
            throw new RuntimeException("Freelancer balance cannot be negative");
        }
        freelancerBalanceRepository.save(freelancer.getBalance());
    }
}
