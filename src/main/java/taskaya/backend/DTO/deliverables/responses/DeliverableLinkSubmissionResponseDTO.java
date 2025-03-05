package taskaya.backend.DTO.deliverables.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverableLinkSubmissionResponseDTO {
    String id;
    String name;
    String url;
}
