package bus_ticket_reservation;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailUtils {

    private static final String EMAIL = "your_email@gmail.com";
    private static final String APP_PASSWORD = "your_app_password";

    public static void sendEmail(String to, String subject, String body) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, APP_PASSWORD);
                }
            });

            // Create email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);

            System.out.println("Email sent successfully!");

        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        sendEmail("friend@example.com", "Hello!", "This is a test email.");
    }
}
