package taskaya.backend.exceptions.error_responses;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
public class GeneralErrorResponse {
    HttpStatus status;
    String message;
    Long timStamp ;
    String Type ;

    public GeneralErrorResponse(String message ,HttpStatus status, String type ){
        this.status = status;
        this.message=message;
        this.timStamp = System.currentTimeMillis();
        this.Type=type;
    }
}
