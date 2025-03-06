package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.deliverables.responses.DeliverableLinkSubmissionResponseDTO;
import taskaya.backend.DTO.jobs.responses.JobSearchResponseDTO;
import taskaya.backend.entity.work.DeliverableLink;
import taskaya.backend.entity.work.Job;

import java.util.LinkedList;
import java.util.List;

@Component
public class DeliverableLinkSubmissionResponseMapper {
    public static DeliverableLinkSubmissionResponseDTO toDTO(DeliverableLink deliverableLink){
        return DeliverableLinkSubmissionResponseDTO.builder()
                .id(deliverableLink.getId().toString())
                .url(deliverableLink.getLinkUrl())
                .name(deliverableLink.getFileName())
                .build();
    }

    public static List<DeliverableLinkSubmissionResponseDTO> toDTOList(List<DeliverableLink> links){
        List<DeliverableLinkSubmissionResponseDTO> result = new LinkedList<>();
        for(DeliverableLink link : links){
            result.add(toDTO(link));
        }
        return result;
    }
}
