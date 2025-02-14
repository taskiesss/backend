package taskaya.backend.services.work;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.repository.work.WorkerEntityRepository;

import java.util.UUID;

@Service
public class WorkerEntityService {
    @Autowired
    private WorkerEntityRepository workerEntityRepository;

    @Transactional
    public void createWorkerEntity(WorkerEntity workerEntity){
        workerEntityRepository.save(workerEntity);
    };

    public WorkerEntity findById(UUID uuid){
        return workerEntityRepository.findById(uuid)
                .orElseThrow(()-> new RuntimeException("Worker Entity Not Found!"));
    }
}
