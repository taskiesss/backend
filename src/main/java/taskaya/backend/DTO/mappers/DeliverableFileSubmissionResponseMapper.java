package taskaya.backend.DTO.mappers;

import org.springframework.stereotype.Component;
import taskaya.backend.DTO.deliverables.responses.DeliverableFileSubmissionResponseDTO;
import taskaya.backend.entity.work.DeliverableFile;


import java.util.LinkedList;
import java.util.List;
@Component
public class DeliverableFileSubmissionResponseMapper {
    public static DeliverableFileSubmissionResponseDTO toDTO(DeliverableFile deliverableFile){
        return DeliverableFileSubmissionResponseDTO.builder()
                .id(deliverableFile.getId().toString())
                .url(deliverableFile.getFilePath())
                .name(deliverableFile.getFileName())
                .build();
    }

    public static List<DeliverableFileSubmissionResponseDTO> toDTOList(List<DeliverableFile> files){
        List<DeliverableFileSubmissionResponseDTO> result = new LinkedList<>();
        for(DeliverableFile file : files){
            result.add(toDTO(file));
        }
        return result;
    }
}
