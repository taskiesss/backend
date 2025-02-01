package taskaya.backend.DTO.login;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginDTO {
    private String email;
    private String password ;
}
