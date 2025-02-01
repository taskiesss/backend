package taskaya.backend.exceptions.login;

public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException (String message){
        super(message);
    }
}
