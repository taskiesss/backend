package taskaya.backend.DTO.clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientPostedJobsResponseDTO {
   private String jobName;
   private String jobId;
   private Date postedAt;
   private Integer proposals;
   private Integer hired;
   private String contractId;

}
