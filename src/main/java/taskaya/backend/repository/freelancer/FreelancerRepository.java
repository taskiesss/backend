package taskaya.backend.repository.freelancer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.User;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.WorkerEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FreelancerRepository extends JpaRepository<Freelancer, UUID> , JpaSpecificationExecutor<Freelancer> {


    public Optional<Freelancer> findFreelancerById(UUID uuid);
    public Optional<Freelancer> findByUser(User user );
    public Optional<Freelancer> findByWorkerEntity(WorkerEntity workerEntity);
}
