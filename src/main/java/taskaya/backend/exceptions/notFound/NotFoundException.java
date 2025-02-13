package taskaya.backend.exceptions.notFound;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message){
        super(message);
    }
}
