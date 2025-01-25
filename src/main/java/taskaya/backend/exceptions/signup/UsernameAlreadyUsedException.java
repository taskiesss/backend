package taskaya.backend.exceptions.signup;

import lombok.Data;

@Data
public class UsernameAlreadyUsedException extends RuntimeException {
    public UsernameAlreadyUsedException(String message) {
        super(message);
    }
}
