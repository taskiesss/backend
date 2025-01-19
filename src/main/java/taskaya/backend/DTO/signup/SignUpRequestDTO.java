package taskaya.backend.DTO.signup;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpRequestDTO {
    private String email;
    private String password;


}