package taskaya.backend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Job;
import taskaya.backend.entity.work.WorkerEntity;

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

    public void sendOtpEmail(String to, String otp)  {
        String subject = "Your Taskaya OTP Code";
        String content = "Hi,<br>You've requested an OTP code to verify your email.<br>\nYour OTP code is:<br><strong>" + otp + "</strong><br>Please do not share this code with anyone.<br>\n" +
                "\n" +
                "If you did not request this code, please ignore this email.<br>\n" +
                "\n" +
                "Sincerely,<br>\n" +
                "\n" +
                " <strong>Taskaya Team</strong><br>";
        try {
            sendEmail(to, subject, content);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendProposalToClient(String to, String from, String jobTitle) {
        String subject = "New Proposal Received";
        String content = "Hello,<br><br>"
                + "You have received a new proposal for the job: <strong>" + jobTitle + "</strong>.<br><br>"
                + "The proposal was sent by: <strong>" + from + "</strong>.<br><br>"
                + "Please review the proposal at your earliest convenience.<br><br>"
                + "If you have any questions or need further details, feel free to reach out.<br><br>"
                + "Best regards,<br>"
                + "<strong>Taskaya Team</strong>";

        try {
            sendEmail(to, subject, content);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendAcceptanceToFreelance(String to, String freelancerName, String communityName) {
        String subject = "Welcome to " + communityName;
        String content = "<html><body>"
                + "Dear " + freelancerName + ",<br><br>"
                + "We are pleased to inform you that your request to join <strong>" + communityName + "</strong> has been approved.<br><br>"
                + "You may now engage with members, access resources, and contribute to discussions.<br><br>"
                + "Should you have any questions, feel free to reach out.<br><br>"
                + "Best regards,<br>"
                + "<strong>Taskaya Team</strong>"
                + "</body></html>";

        try {
            sendEmail(to, subject, content);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendNotificationMailToClientforReviewRequest(String to, String clientName, String freelancerOrCommunityName, String jobTitle, String milestoneName)  {
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

        try {
            sendEmail(to, subject, content);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendMailToFreelancerAfterClientApproval(String to, String freelancerName, String jobTitle, String milestoneName)  {
        String subject = "Milestone Approved: " + milestoneName + " - " + jobTitle;
        String content = "<html><body>"
                + "Dear " + freelancerName + ",<br><br>"
                + "We are pleased to inform you that the milestone <strong>" + milestoneName + "</strong> "
                + "in the project <strong>" + jobTitle + "</strong> has been approved.<br><br>"
                + "Congratulations on this achievement!<br><br>"
                + "If you have any questions or need further details, feel free to reach out.<br><br>"
                + "Best regards,<br>"
                + "<strong>Taskaya Team</strong>"
                + "</body></html>";

        try {
            sendEmail(to, subject, content);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendEmailForFreelancerForStartingContract(String email, Contract contract) {
        String subject = "Contract Activation: " + contract.getJob().getTitle();
        String addedBy = contract.getWorkerEntity().getType() == WorkerEntity.WorkerType.COMMUNITY ? "as a community member" : "";
        String content = "<html><body>"
                + "Dear Freelancer,<br><br>"
                + "We are excited to inform you "+addedBy+" that your contract titled <strong>" + contract.getJob().getTitle() + "</strong> has been activated.<br><br>"
                + "You can now start working on the project and track your progress.<br><br>"
                + "If you have any questions or need further details, feel free to reach out.<br><br>"
                + "Best regards,<br>"
                + "<strong>Taskaya Team</strong>"
                + "</body></html>";

        try {
            sendEmail(email, subject, content);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendEmailForClientForStartingContract(String email, Contract contract) {
        String subject = "Contract Activation: " + contract.getJob().getTitle();
        String content = "<html><body>"
                +"Dear Client,<br><br>"
                + "We are excited to inform you that your contract titled <strong>" + contract.getJob().getTitle() + "</strong> has been activated.<br><br>"
                + "You can now track the progress of the freelancer working on your contracts through your dashboard.<br><br>"
                + "If you have any questions or need further details, feel free to reach out.<br><br>"
                + "Best regards,<br>"
                + "<strong>Taskaya Team</strong>"
                + "</body></html>";

        try {
            sendEmail(email, subject, content);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendRejectionMailToClient(String email, Contract contract) {
        String subject = "Contract Rejection: " + contract.getJob().getTitle();
        String content = "<html><body>"
                + "Dear Client,<br><br>"
                + "We regret to inform you that your contract titled <strong>" + contract.getJob().getTitle() + "</strong>whit  has been rejected.<br><br>"
                + "If you have any questions or need further details, feel free to reach out.<br><br>"
                + "Best regards,<br>"
                + "<strong>Taskaya Team</strong>"
                + "</body></html>";

        try {
            sendEmail(email, subject, content);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendEmailForFreelancerForCreatingContract(String email, Job job) {
        String subject = "New Job Offer: " + job.getTitle();
        String content = "<html><body>"
                + "Dear Freelancer,<br><br>"
                + "Congratulations! You have received a new offer for the job titled <strong>" + job.getTitle() + "</strong>.<br><br>"
                + "Please review the offer details and take the necessary steps to proceed.<br><br>"
                + "We wish you success and a productive collaboration!<br><br>"
                + "Best regards,<br>"
                + "<strong>Taskaya Team</strong>"
                + "</body></html>";

        try {
            sendEmail(email, subject, content);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
