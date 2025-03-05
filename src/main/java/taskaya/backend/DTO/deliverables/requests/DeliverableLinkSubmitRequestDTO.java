package taskaya.backend.DTO.deliverables.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverableLinkSubmitRequestDTO {
    String name;
    String url;
}
