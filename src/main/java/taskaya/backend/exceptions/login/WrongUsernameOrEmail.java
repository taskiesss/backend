package taskaya.backend.exceptions.login;

import lombok.Data;

@Data
public class WrongUsernameOrEmail extends RuntimeException{
    public WrongUsernameOrEmail(String message){
        super(message);
    }
}
