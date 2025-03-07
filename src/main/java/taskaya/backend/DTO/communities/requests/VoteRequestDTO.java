package taskaya.backend.DTO.communities.requests;

import lombok.Data;

@Data
public class VoteRequestDTO {
    private String contractId;
    private Boolean agreed;
}
