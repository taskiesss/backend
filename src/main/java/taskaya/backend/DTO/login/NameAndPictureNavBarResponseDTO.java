package taskaya.backend.DTO.login;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.entity.User;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NameAndPictureNavBarResponseDTO {
    private String profilePicture;
    private String name;
    User.Role role ;
    Integer newNotifications;
    String id ;
}
