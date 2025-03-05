package taskaya.backend.DTO.deliverables.responses;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverableFileSubmissionResponseDTO {
    String id;
    String name;
    String url;
}
