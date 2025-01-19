package taskaya.backend.exceptions.signup;

import lombok.Data;

@Data
public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
