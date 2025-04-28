package taskaya.backend.DTO.mappers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import taskaya.backend.DTO.jobs.responses.JobSearchResponseDTO;

import taskaya.backend.DTO.milestones.responses.MilestonesDetailsResponseDTO;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.Milestone;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Component
public class MilestonesDetailsMapper {
    public static MilestonesDetailsResponseDTO toDTO(Milestone milestone){
        return MilestonesDetailsResponseDTO.builder()
                .milestoneId(milestone.getId().toString())
                .title(milestone.getName())
                .description(milestone.getDescription())
                .status(milestone.getStatus())
                .expectedHours(milestone.getEstimatedHours())
                .dueDate(milestone.getDueDate())
                .build();
    }

    public static List<MilestonesDetailsResponseDTO> toListDTO(List<Milestone> milestones){
        ArrayList<Milestone> milestonesArray = new ArrayList(milestones);
        milestonesArray.sort(Comparator.comparing(Milestone::getNumber));
        List<MilestonesDetailsResponseDTO> result = new LinkedList<>();
        for(Milestone milestone : milestonesArray){
            result.add(toDTO(milestone));
        }
        return result;
    }

    public static Page<MilestonesDetailsResponseDTO> toPageDTO(Page<Milestone> milestones){
        return new PageImpl<>(toListDTO(milestones.getContent()),milestones.getPageable(), milestones.getTotalElements());
    }
}
