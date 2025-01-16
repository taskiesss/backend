package taskaya.backend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * General function to send an email.
     *
     * @param to      Recipient's email address
     * @param subject Email subject
     * @param content Email content (HTML supported)
     * @throws MessagingException if there is an error while sending the email
     */
    public void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    public void sendOtpEmail(String to, String otp) throws MessagingException {
        String subject = "Your OTP Code";
        String content = "<p>Your OTP code is: <strong>" + otp + "</strong></p>";
        sendEmail(to, subject, content);
    }
}
