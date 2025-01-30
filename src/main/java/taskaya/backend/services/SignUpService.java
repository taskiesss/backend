package taskaya.backend.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.signup.SignUpRequestDTO;
import taskaya.backend.DTO.signup.VerifyOtpRequestDTO;
import taskaya.backend.entity.User;
import taskaya.backend.exceptions.signup.EmailAlreadyUsedException;
import taskaya.backend.exceptions.signup.UsernameAlreadyUsedException;
import taskaya.backend.exceptions.signup.WeakPasswordException;
import taskaya.backend.exceptions.signup.WrongRoleException;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.services.client.ClientService;
import taskaya.backend.services.freelancer.FreelancerService;
import taskaya.backend.utility.OTP;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

@Service
public class SignUpService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ClientService clientService;
    @Autowired
    FreelancerService freelancerService;

    private Map<String, OTP> otpCache = new HashMap<>();

    public void isRoleCorrect(String role){
        if(!role.equalsIgnoreCase("client")&&!role.equalsIgnoreCase("freelancer")){
            throw new WrongRoleException("Please enter either client or freelancer as role.");
        }
    }

    public void isUsernameExist(String username) {
        if(userRepository.existsByUsername(username))
            throw new UsernameAlreadyUsedException("this username already exists ");
    }

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

    public String createOtp(SignUpRequestDTO signUpRequestDTO){
        String otpCode = generateOtp();
        OTP otpObject = new OTP(otpCode);
        otpObject.setEmail(signUpRequestDTO.getEmail());
        otpObject.setUsername(signUpRequestDTO.getUsername());
        otpObject.setPassword(signUpRequestDTO.getPassword());


        otpCache.put(signUpRequestDTO.getEmail(),otpObject);
        return otpCode;
    }

    public boolean verifyOtp (VerifyOtpRequestDTO request)throws WrongRoleException{
        OTP cachedOtp = otpCache.get(request.getEmail());

        if (cachedOtp != null &&
                cachedOtp.getOtp().equals(request.getOtp()) &&
                cachedOtp.getUsername().equals(request.getUsername())&&
                cachedOtp.getPassword().equals(request.getPassword())) {
            // Encrypt password
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(request.getPassword());

            // Save user
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(encodedPassword);


            if(request.getRole().equalsIgnoreCase("client")){
                clientService.createClient(user);
            } else if (request.getRole().equalsIgnoreCase("freelancer")) {
                freelancerService.createFreelancer(user);
            }else{
                throw new WrongRoleException("Please enter either client or freelancer as role.");
            }
            // Remove OTP from cache
            otpCache.remove(request.getEmail());

            return true;
        } else {
            return false;
        }    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit OTP
        return String.valueOf(otp);
    }

    public void cleanHashMap(){
        long currentTime = System.currentTimeMillis();
        for (String key : otpCache.keySet()) {
            long time = currentTime-otpCache.get(key).getTimestamp();
            if(time>= 300000){
                otpCache.remove(key);
            }
        }
    }


}
