package taskaya.backend.services.work;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskaya.backend.entity.work.Milestone;
import taskaya.backend.repository.work.MilestoneRepository;

import java.util.List;

@Service
public class MilestoneService {
    @Autowired
    private MilestoneRepository milestoneRepository;

    @Transactional
    public void saveMilestone(Milestone milestone){milestoneRepository.save(milestone);}

    @Transactional
    public void saveAll(List<Milestone> milestone){milestoneRepository.saveAll(milestone);}

    public Milestone getMilestone(List<Milestone> milestones,int number){
        for(Milestone milestone : milestones){
            if(milestone.getNumber() == number){
                return milestone;
            }
        }
        throw new IllegalArgumentException("Milestone number doesn't exist.");
    }
}
