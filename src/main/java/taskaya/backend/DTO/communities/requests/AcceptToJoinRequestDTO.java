package taskaya.backend.DTO.communities.requests;

import lombok.Data;

@Data
public class AcceptToJoinRequestDTO {
    private String freelancerId;
    private String positionName;
    private String choice;
}
