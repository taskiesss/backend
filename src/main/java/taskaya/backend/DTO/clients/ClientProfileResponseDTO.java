package taskaya.backend.DTO.clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientProfileResponseDTO {
    private String uuid;
    private String name;
    private String username;
    private String country;
    private float rate;
    private List<String> skills;
    private List<String> languages;
    private String description;
    private String profilePicture;
    private String coverPhoto;
    private Integer completedJobs;
    private double totalSpent;
}
