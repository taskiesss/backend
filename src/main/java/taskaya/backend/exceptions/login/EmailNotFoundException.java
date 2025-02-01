package taskaya.backend.exceptions.login;

import lombok.Data;

@Data
public class EmailNotFoundException extends RuntimeException{
    public  EmailNotFoundException(String message){
        super(message);
    }
}
