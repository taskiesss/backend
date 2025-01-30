package taskaya.backend.utility;

import lombok.Data;

@Data
public class OTP {
    private String otp ;
    private String email;
    private String username;
    private String password ;
    private String role;
    private Long timestamp;

    public OTP(String otp){
        this.otp = otp;
        timestamp = System.currentTimeMillis();
    }
}
