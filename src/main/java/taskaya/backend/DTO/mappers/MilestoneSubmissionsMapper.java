package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.milestones.responses.MilestoneSubmissionResponseDTO;
import taskaya.backend.entity.work.Milestone;

@Component
public class MilestoneSubmissionsMapper {
    public static MilestoneSubmissionResponseDTO toDTO(Milestone milestone){
        return MilestoneSubmissionResponseDTO.builder()
                .description(milestone.getDescription())
                .files(DeliverableFileSubmissionResponseMapper.toDTOList(milestone.getDeliverableFiles()))
                .links(DeliverableLinkSubmissionResponseMapper.toDTOList(milestone.getDeliverableLinks()))
                .build();
    }
}
