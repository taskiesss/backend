package taskaya.backend.utility;

import lombok.Data;

@Data
public class OTP {
    String otp ;
    Long timestamp;

    public OTP(String otp){
        this.otp = otp;
        timestamp = System.currentTimeMillis();
    }
}
