package org.hospiconnect.controller.User;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {
    private static final String USERNAME = "triptogo2025@gmail.com";
    private static final String PASSWORD = "rove ikbd nqpl xlnv";

    public static void sendVerificationCode(String recipient, String code) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(MimeUtility.encodeText("Code de vérification pour Hospiconnecte : " + code, "UTF-8", "B"));

            MimeBodyPart htmlPart = new MimeBodyPart();
            String emailTemplate = "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #F4F7FA; text-align: center; padding: 40px; }" +
                    ".container { background: white; padding: 30px; border-radius: 12px; " +
                    "box-shadow: 0px 5px 20px rgba(0, 0, 0, 0.15); max-width: 520px; margin: auto; text-align: center; }" +
                    "h2 { color: #132A3E; font-size: 24px; margin-bottom: 20px; }" +
                    "p { font-size: 18px; color: #444; line-height: 1.6; margin-bottom: 25px; }" +
                    ".info { font-size: 16px; color: #666; margin-bottom: 20px; }" +
                    ".code { font-size: 28px; font-weight: bold; color: #4CAF50; margin: 20px 0; }" + // Couleur plus "santé"
                    ".footer { font-size: 12px; color: #888; margin-top: 25px; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<h2>Vérification de votre compte Hospiconnecte</h2>" +
                    "<div class='info'>Adresse email : <strong>" + recipient + "</strong></div>" +
                    "<div class='info'>Code de vérification :</div>" +
                    "<div class='code'>" + code + "</div>" +
                    "<p>Utilisez ce code pour sécuriser votre accès.</p>" +
                    "<p>Ce code expirera dans quelques minutes pour votre sécurité.</p>" +
                    "<p class='footer'>© 2025 Hospiconnecte. Tous droits réservés.</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            htmlPart.setContent(emailTemplate, "text/html; charset=UTF-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(htmlPart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("✅ Email de code de vérification Hospiconnecte envoyé avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors de l'envoi de l'email de vérification Hospiconnecte.");
        }
    }
}
