package taskaya.backend.services.freelancer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskaya.backend.entity.enums.ExperienceLevel;
import taskaya.backend.entity.freelancer.FreelancerBusiness;
import taskaya.backend.repository.freelancer.FreelancerBusinessRepository;


@Service
public class FreelancerBusinessService {
    @Autowired
    private FreelancerBusinessRepository freelancerBusinessRepository;

    public ExperienceLevel incrementCompletedJobs(FreelancerBusiness freelancerBusiness) {
        freelancerBusiness.setCompletedJobs(freelancerBusiness.getCompletedJobs() + 1);
        freelancerBusinessRepository.save(freelancerBusiness);
        return getExperienceLevel(freelancerBusiness.getCompletedJobs());
    }

    private ExperienceLevel getExperienceLevel(Integer completedJobs) {
        if (completedJobs < 5) {
            return ExperienceLevel.entry_level;
        } else if (completedJobs < 10) {
            return ExperienceLevel.intermediate;
        } else {
            return ExperienceLevel.expert;
        }
    }
}
