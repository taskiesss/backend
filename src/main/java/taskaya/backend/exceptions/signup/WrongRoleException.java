package taskaya.backend.exceptions.signup;

import lombok.Data;

@Data
public class WrongRoleException extends RuntimeException {
    public WrongRoleException(String message){super(message);}
}
