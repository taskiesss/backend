package taskaya.backend.exceptions.notFound;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import taskaya.backend.exceptions.error_responses.GeneralErrorResponse;

@ControllerAdvice
public class NotFoundHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> NotFoundHandler(RuntimeException e){
        GeneralErrorResponse errorResponse = new GeneralErrorResponse(e.getMessage(),HttpStatus.NOT_FOUND,"not found");
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
