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
import taskaya.backend.utility.OTP;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/signup")
public class SignUpController {

    @Autowired
    private MailService mailService;


    @Autowired
    private SignUpService signUpService;

    @PostMapping()
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDTO request)  {

        //check if role is written correctly
        signUpService.isRoleCorrect(request.getRole());
        //check if username already exists
        signUpService.isUsernameExist(request.getUsername());
        //check if mail not already used in database
        signUpService.isEmailExist(request.getEmail());
        //password is good
        signUpService.isStrongPassword(request.getPassword());


        return ResponseEntity.ok(true);
    }

    @PostMapping("send-otp")
    public ResponseEntity<?> getOtp(@RequestBody SignUpRequestDTO request) throws MessagingException {
        // Generate OTP
        String otp = signUpService.createOtp(request.getEmail());

        // Send OTP to email
        mailService.sendOtpEmail(request.getEmail(), otp);

        return ResponseEntity.ok(true);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequestDTO request) {
        boolean isOtpRight = signUpService.verifyOtp(request);

        if (isOtpRight) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(new GeneralErrorResponse("wrong otp ", HttpStatus.BAD_REQUEST,"invalid_otp"));
        }
    }


}