package lk.ijse.theserenitymentalhealththerapycenter.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class MailUtil {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    
    // NOTE: Update these configuration placeholders with valid Gmail credentials/App Password.
    private static final String SENDER_EMAIL = "kavindusasanka11@gmail.com";
    private static final String SENDER_PASSWORD = "qporsopqlowppaid";

    public static void sendOTP(String recipientEmail, String otp) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SENDER_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Serenity Mental Health Therapy Center - Password Reset OTP");
        
        String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; max-width: 500px;'>"
                + "<h2 style='color: #2e7d32;'>Password Reset Verification</h2>"
                + "<p>Hello,</p>"
                + "<p>You requested a password reset for your account at <strong>Serenity Mental Health Therapy Center</strong>.</p>"
                + "<p>Please use the following 6-digit One Time Password (OTP) to verify your request:</p>"
                + "<div style='font-size: 24px; font-weight: bold; background-color: #f4f6f8; padding: 10px 20px; text-align: center; border-radius: 4px; letter-spacing: 2px; color: #1b5e20; margin: 20px 0;'>"
                + otp + "</div>"
                + "<p>This code is valid for single use. If you did not initiate this request, you can safely ignore this email.</p>"
                + "<hr style='border: 0; border-top: 1px solid #eeeeee;' />"
                + "<p style='font-size: 12px; color: #888888;'>This is an automated message. Please do not reply to this email.</p>"
                + "</div>";

        message.setContent(htmlContent, "text/html");
        Transport.send(message);
    }
}
