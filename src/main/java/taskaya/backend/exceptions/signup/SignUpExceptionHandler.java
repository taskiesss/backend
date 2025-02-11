package taskaya.backend.exceptions.signup;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import taskaya.backend.exceptions.error_responses.GeneralErrorResponse;


@ControllerAdvice
public class SignUpExceptionHandler {


    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<?> weakPasswordHandler(RuntimeException e){
        GeneralErrorResponse errorResponse = new GeneralErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST,"password");
        return ResponseEntity.badRequest().body(errorResponse);
    }
    @ExceptionHandler(WrongRoleException.class)
    public ResponseEntity<?> roleHandler(RuntimeException e){
        GeneralErrorResponse errorResponse = new GeneralErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST,"role");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<?> mailAlreadyUsedHandler(RuntimeException e){
        GeneralErrorResponse errorResponse = new GeneralErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST,"email");
        return ResponseEntity.badRequest().body(errorResponse);
    }



    @ExceptionHandler(UsernameAlreadyUsedException.class)
    public ResponseEntity<?> usenameAlreadyUsedHandler(RuntimeException e){
        GeneralErrorResponse errorResponse = new GeneralErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST,"username");
        return ResponseEntity.badRequest().body(errorResponse);
    }


}
