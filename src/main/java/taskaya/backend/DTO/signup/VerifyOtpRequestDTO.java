package taskaya.backend.DTO.signup;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class VerifyOtpRequestDTO {
    private String role;
    private String username;
    private String email;
    private String otp;
    private String password;

}