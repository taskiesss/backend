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


    public void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    public void sendOtpEmail(String to, String otp) throws MessagingException {
        String subject = "Your Taskaya OTP Code";
        String content = "Hi,<br>You've requested an OTP code to verify your email.<br>\nYour OTP code is:<br><strong>" + otp + "</strong><br>Please do not share this code with anyone.<br>\n" +
                "\n" +
                "If you did not request this code, please ignore this email.<br>\n" +
                "\n" +
                "Sincerely,<br>\n" +
                "\n" +
                " <strong>Taskaya Team</strong><br>";
        sendEmail(to, subject, content);
    }

    public void sendProposalToClient(String to, String from, String jobTitle) throws MessagingException{
        String subject = "New Proposal Received";
        String content = "Hello,<br><br>"
                + "You have received a new proposal for the job: <strong>" + jobTitle + "</strong>.<br><br>"
                + "The proposal was sent by: <strong>" + from + "</strong>.<br><br>"
                + "Please review the proposal at your earliest convenience.<br><br>"
                + "If you have any questions or need further details, feel free to reach out.<br><br>"
                + "Best regards,<br>"
                + "<strong>Taskaya Team</strong>";

        sendEmail(to, subject, content);
    }

    public void sendAcceptanceToFreelance(String to, String freelancerName, String communityName) throws MessagingException{
        String subject = "Welcome to " + communityName;
        String content = "<html><body>"
                + "Dear " + freelancerName + ",<br><br>"
                + "We are pleased to inform you that your request to join <strong>" + communityName + "</strong> has been approved.<br><br>"
                + "You may now engage with members, access resources, and contribute to discussions.<br><br>"
                + "Should you have any questions, feel free to reach out.<br><br>"
                + "Best regards,<br>"
                + "<strong>Taskaya Team</strong>"
                + "</body></html>";

        sendEmail(to, subject, content);
    }

    public void sendNotificationMailToClient(String to, String clientName, String freelancerOrCommunityName, String jobTitle, String milestoneName) throws MessagingException {
        String subject = "Milestone Review Request: " + milestoneName + " - " + jobTitle;
        String content = "<html><body>"
                + "Dear " + clientName + ",<br><br>"
                + "We would like to inform you that <strong>" + freelancerOrCommunityName + "</strong> has requested a review for the milestone <strong>" + milestoneName + "</strong> "
                + "in the project <strong>" + jobTitle + "</strong>.<br><br>"
                + "Please take a moment to review the milestone and provide your feedback or approval.<br><br>"
                + "If you have any questions or need further details, feel free to reach out.<br><br>"
                + "Best regards,<br>"
                + "<strong>Taskaya Team</strong>"
                + "</body></html>";

        sendEmail(to, subject, content);
    }


}
