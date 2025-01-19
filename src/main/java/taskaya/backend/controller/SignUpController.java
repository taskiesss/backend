package taskaya.backend.controller;
// Import necessary packages
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import taskaya.backend.DTO.signup.SignUpRequestDTO;
import taskaya.backend.DTO.signup.VerifyOtpRequestDTO;
import taskaya.backend.entity.User;
import taskaya.backend.exceptions.error_responses.GeneralErrorResponse;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.services.MailService;
import taskaya.backend.services.SignUpService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/signup")
public class SignUpController {

    @Autowired
    private MailService mailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SignUpService signUpService;

    private Map<String, String> otpCache = new HashMap<>();

    @PostMapping
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDTO request)  {

        //check if mail not already used in database
        signUpService.isEmailExist(request.getEmail());
        //password is good
        signUpService.isStrongPassword(request.getPassword());


        return ResponseEntity.ok("");
    }

    @PostMapping("send-otp")
    public ResponseEntity<?> getOtp(@RequestBody SignUpRequestDTO request) throws MessagingException {
        // Generate OTP
        String otp = generateOtp();

        // Cache OTP against the email
        otpCache.put(request.getEmail(), otp);

        // Send OTP to email
        mailService.sendOtpEmail(request.getEmail(), otp);

        return ResponseEntity.ok("");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequestDTO request) {
        String cachedOtp = otpCache.get(request.getEmail());

        if (cachedOtp != null && cachedOtp.equals(request.getOtp())) {
            // Encrypt password
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(request.getPassword());

            // Save user
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(encodedPassword);
            userRepository.save(user);

            // Remove OTP from cache
            otpCache.remove(request.getEmail());

            return ResponseEntity.ok("");
        } else {
            return ResponseEntity.badRequest().body(new GeneralErrorResponse("wrong otp ", HttpStatus.BAD_REQUEST));
        }
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit OTP
        return String.valueOf(otp);
    }
}