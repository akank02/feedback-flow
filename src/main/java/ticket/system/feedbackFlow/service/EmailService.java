package ticket.system.feedbackFlow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ticket.system.feedbackFlow.enums.Status;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendStatusUpdateEmail(String toEmail,
                                      String feedbackTitle,
                                      Status newStatus) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your feedback status has been updated");
       message.setText("""
        Hello,
        Your feedback "%s" has been updated to: %s

        Login to view more details.

        FeedbackFlow Team
        """.formatted(feedbackTitle, newStatus.name()));

        mailSender.send(message);
    }

    @Async
    public void sendAdminResponseEmail(String toEmail,
                                       String feedbackTitle,
                                       String adminMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Admin has responded to your feedback");
        message.setText(
                "Hello,\n\n" +
                "An admin has responded to your feedback " +
                "\"" + feedbackTitle + "\":\n\n" +
                "\"" + adminMessage + "\"\n\n" +
                "Login to view the full conversation.\n\n" +
                "FeedbackFlow Team"
        );

        mailSender.send(message);
    }
}