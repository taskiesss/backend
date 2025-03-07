package taskaya.backend.DTO.communities.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityOfferResponseDTO {
    private String contractID;
    private String jobTitle;
    private String description;
    private List<String> skills;
    private Double pricePerHour;
    private Date sentDate;
    private Date dueDate;
    private int voted;
    private int left;
    private int accepted;
    private int rejected;
    private Boolean agreed;

}
