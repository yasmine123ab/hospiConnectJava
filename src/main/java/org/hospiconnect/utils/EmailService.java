package org.hospiconnect.utils;

import org.hospiconnect.model.Consultation;
import org.hospiconnect.service.ConsultationService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for handling email notifications for consultations and appointments
 */
public class EmailService {

    private static final String HOSPITAL_EMAIL = "safwenhaboubi@gmail.com"; // Configured with user's email
    private static final String EMAIL_PASSWORD = "szco wvat dzyu dlmh"; // App password for Gmail
    private static final String GEMINI_API_KEY = "AIzaSyBAJpdDvDlIQ-9_DTaZkM9VyT3SkN1nuKs"; // Gemini API key
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    private static ConsultationService consultationService = new ConsultationService();
    
    /**
     * Sends an email to the specified recipient
     * 
     * @param to Recipient email address
     * @param subject Email subject
     * @param body Email body content
     * @return boolean indicating if the email was sent successfully
     */
    public static boolean sendEmail(String to, String subject, String body) {
        // Email account configuration (should be stored securely in production)
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        
        // Create a session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(HOSPITAL_EMAIL, EMAIL_PASSWORD);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(HOSPITAL_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            
            Transport.send(message);
            System.out.println("Email sent successfully to: " + to);
            
            return true;
        } catch (MessagingException e) {
            System.out.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Checks for upcoming consultations and sends reminders to patients with email addresses
     * 10 hours before the consultation time
     */
    public static void checkAndSendConsultationReminders() {
        System.out.println("Checking for upcoming consultations...");
        
        // Get all consultations
        List<Consultation> consultations = consultationService.getAllConsultations();
        
        // Current time for comparison
        LocalDateTime now = LocalDateTime.now();
        
        for (Consultation consultation : consultations) {
            // Skip consultations without email addresses
            if (consultation.getEmail() == null || consultation.getEmail().trim().isEmpty()) {
                continue;
            }
            
            LocalDate consultationDate = consultation.getDateConsultation();
            
            // As we don't have time in the Consultation model, we'll assume all appointments are at 9:00 AM
            LocalDateTime appointmentDateTime = LocalDateTime.of(consultationDate, LocalTime.of(9, 0));
            
            // Calculate hours until appointment
            long hoursUntilAppointment = ChronoUnit.HOURS.between(now, appointmentDateTime);
            
            // If it's time to send a reminder (10 hours before appointment, with a 15-minute window)
            if (hoursUntilAppointment > 9 && hoursUntilAppointment <= 10) {
                System.out.println("Sending reminder for consultation: " + consultation.getId());
                
                // Generate appointment information with Gemini API
                String appointmentDetails = generateAppointmentInfoWithAI(consultation);
                
                // Create email content
                String subject = "Reminder: Your appointment tomorrow";
                String body = String.format(
                        "Dear %s %s,%n%n" +
                        "This is a reminder that you have an appointment with us tomorrow at 9:00 AM." +
                        "%n%nType: %s" +
                        "%n%nPlease arrive 15 minutes early to complete any necessary paperwork." +
                        "%n%nIf you need to cancel or reschedule, please call us as soon as possible." +
                        "%n%nAdditional Information:%n%s" +
                        "%n%nThank you,%nHospiConnect Medical Team",
                        consultation.getFirstname(),
                        consultation.getLastname(),
                        consultation.getTypeConsultation(),
                        appointmentDetails
                );
                
                // Send the reminder email
                sendEmail(consultation.getEmail(), subject, body);
            }
        }
    }
    
    /**
     * Send a test reminder email to the specified consultation
     * 
     * @param consultation The consultation to send a reminder for
     * @return Whether the email was sent successfully
     */
    public static boolean sendTestReminderEmail(Consultation consultation) {
        if (consultation == null || consultation.getEmail() == null || consultation.getEmail().trim().isEmpty()) {
            return false;
        }
        
        // Generate appointment information with Gemini API
        String appointmentDetails = generateAppointmentInfoWithAI(consultation);
        
        // Create email content
        String subject = "Test Reminder: Your upcoming appointment";
        String body = String.format(
                "Dear %s %s,%n%n" +
                "This is a TEST reminder for your upcoming appointment on %s at 9:00 AM." +
                "%n%nType: %s" +
                "%n%nPlease arrive 15 minutes early to complete any necessary paperwork." +
                "%n%nIf you need to cancel or reschedule, please call us as soon as possible." +
                "%n%nAdditional Information:%n%s" +
                "%n%nThank you,%nHospiConnect Medical Team (TEST EMAIL)",
                consultation.getFirstname(),
                consultation.getLastname(),
                consultation.getDateConsultation().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                consultation.getTypeConsultation(),
                appointmentDetails
        );
        
        // Send the test reminder email
        return sendEmail(consultation.getEmail(), subject, body);
    }
    
    /**
     * Generate AI-enhanced appointment information using the Gemini API
     * 
     * @param consultation The consultation to generate information for
     * @return AI-generated appointment information
     */
    private static String generateAppointmentInfoWithAI(Consultation consultation) {
        try {
            // Prepare the API request
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create prompt for Gemini
            String prompt = String.format(
                "Generate a short paragraph (3-4 sentences) with helpful information for a patient preparing for a %s appointment. " +
                "Include what they should bring, any preparation needed, and one general health tip. " +
                "Keep it reassuring and friendly.",
                consultation.getTypeConsultation()
            );

            // Create request JSON
            String requestBody = String.format(
                "{\"contents\": [{\"parts\":[{\"text\": \"%s\"}]}]}",
                prompt.replace("\"", "\\\"") // Escape quotes in the prompt
            );

            // Send the request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            // Extract the text from the JSON response
            String jsonResponse = response.toString();
            int textStartIndex = jsonResponse.indexOf("\"text\": \"") + 9;
            int textEndIndex = jsonResponse.indexOf("\"", textStartIndex);
            
            if (textStartIndex >= 9 && textEndIndex > textStartIndex) {
                return jsonResponse.substring(textStartIndex, textEndIndex);
            } else {
                // If we can't parse the response properly, return a default message
                return "Please bring any relevant medical records and a list of current medications. " +
                       "Arrive well-hydrated and with any necessary referral documents. " +
                       "Remember that getting adequate sleep the night before will help ensure a productive appointment.";
            }
            
        } catch (Exception e) {
            System.out.println("Error using Gemini API: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback text if the API fails
            return "Please bring any relevant medical records and a list of current medications. " +
                   "Arrive well-hydrated and with any necessary referral documents. " +
                   "Remember that getting adequate sleep the night before will help ensure a productive appointment.";
        }
    }
    
    /**
     * Starts the email reminder scheduler
     */
    public static void startReminderScheduler() {
        // Check for reminders once every hour rather than every 10 minutes
        // This is more efficient while still ensuring timely reminders
        scheduler.scheduleAtFixedRate(EmailService::checkAndSendConsultationReminders, 0, 1, TimeUnit.HOURS);
        System.out.println("Email reminder scheduler started - checking once per hour");
    }
    
    /**
     * Stops the email reminder scheduler
     */
    public static void stopReminderScheduler() {
        scheduler.shutdown();
        System.out.println("Email reminder scheduler stopped");
    }
}