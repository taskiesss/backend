package taskaya.backend.exceptions.signup;

import lombok.Data;

@Data

public class WeakPasswordException extends RuntimeException{
    public  WeakPasswordException (String message){
        super(message);
    }

}
