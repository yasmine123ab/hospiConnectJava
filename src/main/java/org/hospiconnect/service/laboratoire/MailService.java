package org.hospiconnect.service.laboratoire;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class MailService {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String username = dotenv.get("MAIL_USERNAME");
    private static final String password = dotenv.get("MAIL_PASSWORD");
    private static final Properties smtpProperties = new Properties();

    private static final MailService instance = new MailService();

    public static MailService getInstance() {
        return instance;
    }

    public MailService() {
        // SMTP properties
        smtpProperties.put("mail.smtp.auth", "true");
        smtpProperties.put("mail.smtp.starttls.enable", "true");
        smtpProperties.put("mail.smtp.host", "smtp.gmail.com");
        smtpProperties.put("mail.smtp.port", "587");
    }

    public void sendEmail(String to, String subject, String body) {
        // Sender's email credentials

        // Create session with authenticator
        Session session = Session.getInstance(smtpProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            // Send message
            Transport.send(message);
            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

}
