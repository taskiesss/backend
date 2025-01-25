package taskaya.backend.exceptions.signup;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import taskaya.backend.exceptions.error_responses.GeneralErrorResponse;


@ControllerAdvice
public class SignUpExceptionHandler {


    @ExceptionHandler({WeakPasswordException.class, EmailAlreadyUsedException.class, WrongRoleException.class, UsernameAlreadyUsedException.class})
    public ResponseEntity<?> weakPasswordHandler(RuntimeException e){
        GeneralErrorResponse errorResponse = new GeneralErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.badRequest().body(errorResponse);
    }

}
