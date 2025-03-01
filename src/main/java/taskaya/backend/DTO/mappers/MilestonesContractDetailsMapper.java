package taskaya.backend.DTO.mappers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.jobs.responses.JobSearchResponseDTO;
import taskaya.backend.DTO.milestones.responses.MilestonesContractDetailsResponseDTO;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.Milestone;

import java.util.LinkedList;
import java.util.List;

@Component
public class MilestonesContractDetailsMapper {
    public static MilestonesContractDetailsResponseDTO toDTO(Milestone milestone){
        return MilestonesContractDetailsResponseDTO.builder()
                .milestoneId(milestone.getId().toString())
                .title(milestone.getName())
                .description(milestone.getDescription())
                .status(milestone.getStatus())
                .expectedHours(milestone.getEstimatedHours())
                .dueDate(milestone.getDueDate())
                .build();
    }

    public static List<MilestonesContractDetailsResponseDTO> toListDTO(List<Milestone> milestones){
        List<MilestonesContractDetailsResponseDTO> result = new LinkedList<>();
        for(Milestone milestone : milestones){
            result.add(toDTO(milestone));
        }
        return result;
    }

    public static Page<MilestonesContractDetailsResponseDTO> toPageDTO(Page<Milestone> milestones){
        return new PageImpl<>(toListDTO(milestones.getContent()),milestones.getPageable(), milestones.getTotalElements());
    }
}
