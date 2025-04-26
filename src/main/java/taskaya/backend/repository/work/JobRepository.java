package taskaya.backend.repository.work;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.WorkerEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID>, JpaSpecificationExecutor<Job> {
    //to find all not assigned jobs with specifications
//    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.assignedTo WHERE j.assignedTo IS NULL")
//    Page<Job> findAllByAssignedToIsNull(Specification<Job> spec, Pageable pageable);
    Optional<Job> findByTitle(String title);
    List<Job> findByAssignedToAndStatus(WorkerEntity assignedTo, Job.JobStatus status);
    List<Job> findByClientAndStatus(Client client, Job.JobStatus jobStatus);
}


