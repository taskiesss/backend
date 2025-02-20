package taskaya.backend.repository.work;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskaya.backend.entity.work.Proposal;
import taskaya.backend.entity.work.WorkerEntity;

import java.util.UUID;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, UUID> {
    Page<Proposal> findByWorkerEntity(WorkerEntity workerEntity, Pageable pageable);
}
