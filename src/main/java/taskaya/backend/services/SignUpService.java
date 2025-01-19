package taskaya.backend.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskaya.backend.exceptions.signup.EmailAlreadyUsedException;
import taskaya.backend.exceptions.signup.WeakPasswordException;
import taskaya.backend.repository.UserRepository;

import java.util.regex.Pattern;

@Service
public class SignUpService {

    @Autowired
    UserRepository userRepository;

    public void isEmailExist(String email){
        if(userRepository.existsByEmail(email))
            throw  new EmailAlreadyUsedException("this email is already used ");

    }

    public  void isStrongPassword(String password) throws WeakPasswordException {
        boolean isStrong;
        if (password == null || password.length() < 8) {
            isStrong = false; // Must be at least 8 characters long
        }

        // Define regex patterns
        String upperCasePattern = ".*[A-Z].*";      // At least one uppercase letter
        String lowerCasePattern = ".*[a-z].*";      // At least one lowercase letter
        String digitPattern = ".*\\d.*";            // At least one digit
        String specialCharPattern = ".*[!@#$%^&*(),.?\":{}|<>].*"; // At least one special character

        // Validate against patterns
        boolean hasUpperCase = Pattern.matches(upperCasePattern, password);
        boolean hasLowerCase = Pattern.matches(lowerCasePattern, password);
        boolean hasDigit = Pattern.matches(digitPattern, password);
        boolean hasSpecialChar = Pattern.matches(specialCharPattern, password);

        // Password is strong if all conditions are met
        isStrong =  hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;

        if (!isStrong)
            throw  new WeakPasswordException("your password is weak ,enter a strong password");
    }


}
