package taskaya.backend.exceptions.login;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import taskaya.backend.exceptions.error_responses.GeneralErrorResponse;

@ControllerAdvice
public class LogInExceptionHandler {


    @ExceptionHandler(WrongUsernameOrEmail.class)
    public ResponseEntity<?> emailNotFoundHandler(RuntimeException e){
        GeneralErrorResponse errorResponse = new GeneralErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST,"email");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<?> wrongPasswordHandler(RuntimeException e){
        GeneralErrorResponse errorResponse = new GeneralErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST,"password");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(FirstTimeFreelancerFormException.class)
    public ResponseEntity<?> firstTimeFreelancerhandler(RuntimeException e){
        GeneralErrorResponse errorResponse = new GeneralErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST,"form");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> anyRuntimeExceptionHandler(RuntimeException e){
        GeneralErrorResponse errorResponse = new GeneralErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST,"runtime");
        System.out.println(errorResponse);
        return ResponseEntity.badRequest().body(errorResponse);
    }


    //doesnt work ???!!!
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> security (RuntimeException e){
        GeneralErrorResponse errorResponse = new GeneralErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST,"security");
        System.out.println(errorResponse);
        return ResponseEntity.status(401).body(errorResponse);
    }
}
