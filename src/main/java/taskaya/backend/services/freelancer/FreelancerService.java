package taskaya.backend.services.freelancer;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskaya.backend.entity.User;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.freelancer.FreelancerBalance;
import taskaya.backend.entity.freelancer.FreelancerBusiness;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.repository.freelancer.FreelancerRepository;

@Service
public class FreelancerService {
    @Autowired
    FreelancerRepository freelancerRepository;

    @Transactional
    public Freelancer createFreelancer(User user){

        user.setRole(User.Role.FREELANCER);

        WorkerEntity workerEntity = WorkerEntity.builder()
                .type(WorkerEntity.WorkerType.FREELANCER)
                .build();

        Freelancer freelancer = Freelancer.builder()
                .id(user.getId())
                .user(user)
                .workerEntity(workerEntity)
                .balance(new FreelancerBalance())
                .freelancerBusiness(new FreelancerBusiness())
                .build();
        freelancerRepository.save(freelancer);
        return freelancer;
    }
}
