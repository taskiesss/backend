package taskaya.backend.exceptions.login;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import taskaya.backend.exceptions.error_responses.GeneralErrorResponse;

import java.time.LocalDateTime;
import java.util.Map;

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
//    @ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity<?> security (RuntimeException e){
//        GeneralErrorResponse errorResponse = new GeneralErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST,"security");
//        System.out.println(errorResponse);
//        return ResponseEntity.status(401).body(errorResponse);
//    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> response = Map.of(
                "timestamp",  System.currentTimeMillis(),
                "status", 401,
                "error", "Forbidden",
                "message", "You do not have permission to access this resource: " + ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> response = Map.of(
                "timestamp",  System.currentTimeMillis(),
                "status", 400,
                "error", "Forbidden",
                "message", "You do not have permission to access this resource."
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
