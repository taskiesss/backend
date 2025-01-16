package taskaya.backend.controller;
// Import necessary packages
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import taskaya.backend.DTO.signup.SignUpRequestDTO;
import taskaya.backend.DTO.signup.VerifyOtpRequestDTO;
import taskaya.backend.entity.User;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.services.MailService;

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

    private Map<String, String> otpCache = new HashMap<>();

    @PostMapping
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDTO request) throws MessagingException {
        // Generate OTP
        String otp = generateOtp();

        // Cache OTP against the email
        otpCache.put(request.getEmail(), otp);

        // Send OTP to email
        mailService.sendOtpEmail(request.getEmail(), otp);

        return ResponseEntity.ok("done");
    }

    @PostMapping("/verify")
    public String verifyOtp(@RequestBody VerifyOtpRequestDTO request) {
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

            return "User registered successfully.";
        } else {
            return "Invalid OTP.";
        }
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit OTP
        return String.valueOf(otp);
    }
}