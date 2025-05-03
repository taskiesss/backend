package taskaya.backend.repository.work;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.Proposal;
import taskaya.backend.entity.work.WorkerEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, UUID>, JpaSpecificationExecutor<Proposal> {
    Page<Proposal> findByWorkerEntity(WorkerEntity workerEntity, Pageable pageable);
    List<Proposal> findByWorkerEntityInAndStatusAndJob(List<WorkerEntity> workerEntities, Proposal.ProposalStatus proposalStatus, Job job);

    List<Proposal> findByJob(Job job);
    Optional<Proposal> findByContract(Contract contract);
    int countByJob(Job job);
}
