package taskaya.backend.services.work;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.repository.work.WorkerEntityRepository;
import taskaya.backend.entity.community.CommunityMember;


import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class WorkerEntityService {
    @Autowired
    private WorkerEntityRepository workerEntityRepository;
    @Autowired
    private FreelancerRepository freelancerRepository;
    @Autowired
    private CommunityRepository communityRepository;

    @Transactional
    public void createWorkerEntity(WorkerEntity workerEntity){
        workerEntityRepository.save(workerEntity);
    };

    public WorkerEntity findById(UUID uuid){
        return workerEntityRepository.findById(uuid)
                .orElseThrow(()-> new RuntimeException("Worker Entity Not Found!"));
    }
    public List<Freelancer> getFreelancersByWorkerEntity(WorkerEntity workerEntity) {
        if (workerEntity.getType() == WorkerEntity.WorkerType.FREELANCER) {
            // assuming FreelancerRepository has a method: findByWorkerEntity
            Freelancer freelancer = freelancerRepository.findByWorkerEntity(workerEntity)
                    .orElseThrow(() -> new RuntimeException("Freelancer not found"));
            return List.of(freelancer);
        } else if (workerEntity.getType() == WorkerEntity.WorkerType.COMMUNITY) {
            // assuming CommunityRepository has a method: findByWorkerEntity
            Community community = communityRepository.findByWorkerEntity(workerEntity)
                    .orElseThrow(() -> new RuntimeException("Community not found"));

            return community.getCommunityMembers().stream()
                    .map(CommunityMember::getFreelancer)
                    .filter(Objects::nonNull)
                    .toList();
        } else {
            throw new IllegalArgumentException("Unknown worker entity type");
        }
    }

}
