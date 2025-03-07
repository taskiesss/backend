package taskaya.backend.repository.work;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.Milestone;
import taskaya.backend.entity.work.WorkerEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> , JpaSpecificationExecutor<Contract> {
    public Optional<Contract> findByJob(Job job);
    Page<Contract> findAllByStatusAndWorkerEntiy(Contract.ContractStatus status, WorkerEntity workerEntity);

}
