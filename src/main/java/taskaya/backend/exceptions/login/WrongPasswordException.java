package taskaya.backend.exceptions.login;

import lombok.Data;

@Data
public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException (String message){
        super(message);
    }
}
