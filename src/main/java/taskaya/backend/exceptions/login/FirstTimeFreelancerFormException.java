package taskaya.backend.exceptions.login;

import lombok.Data;

@Data
public class FirstTimeFreelancerFormException extends RuntimeException{
    public FirstTimeFreelancerFormException (String message) {super(message);}
}