//package utils;
//
//import jakarta.mail.*;
//import jakarta.mail.internet.*;
//import jakarta.activation.*;
//
//import java.util.Properties;
//
//public class EmailReportSender {
//
//    // configure for your account
//    private static final String SMTP_HOST = "smtp.office365.com";
//    private static final int SMTP_PORT = 587;
//    private static final String FROM_EMAIL = "chetan.patil@dharbor.com";
//    private static final String FROM_PASSWORD = "ABCD@2025"; // app password or actual if allowed
//
//    public static void sendReport(String toEmail, String reportPath) {
//        if (reportPath == null) {
//            System.err.println("No report found to send.");
//            return;
//        }
//
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", SMTP_HOST);
//        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
//        props.put("mail.smtp.ssl.trust", SMTP_HOST);
//
//        Session session = Session.getInstance(props, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
//            }
//        });
//
//        try {
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(FROM_EMAIL));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
//            message.setSubject("Automation Test Report");
//
//            // body
//            BodyPart text = new MimeBodyPart();
//            text.setText("Hi Team,\n\nPlease find attached the latest automation report.\n\nRegards,\nAutomation Bot");
//
//            // attachment
//            MimeBodyPart attachment = new MimeBodyPart();
//            DataSource source = new FileDataSource(reportPath);
//            attachment.setDataHandler(new DataHandler(source));
//            attachment.setFileName(new java.io.File(reportPath).getName());
//
//            Multipart multipart = new MimeMultipart();
//            multipart.addBodyPart(text);
//            multipart.addBodyPart(attachment);
//
//            message.setContent(multipart);
//
//            Transport.send(message);
//            System.out.println("✅ Report emailed to " + toEmail + " (attached: " + reportPath + ")");
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }
//}








//package utils;
//
//import jakarta.mail.*;
//import jakarta.mail.internet.*;
//import jakarta.activation.*;
//import java.io.File;
//import java.util.Properties;
//
//public class EmailReportSender {
//
//    private static final String SMTP_HOST = "smtp.office365.com";
//    private static final int SMTP_PORT = 587;
//    private static final String FROM_EMAIL = "chetan.patil@dharbor.com";
//    private static final String FROM_PASSWORD = System.getenv(""); // 👈 no password in code
//
//    public static void sendReport(String toEmail, String reportPath) {
//        if (reportPath == null || !new File(reportPath).exists()) {
//            System.err.println("⚠️ Report file not found: " + reportPath);
//            return;
//        }
//
//        if (FROM_PASSWORD == null) {
//            System.err.println("⚠️ Missing Outlook password! Please set environment variable OUTLOOK_PASS.");
//            return;
//        }
//
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", SMTP_HOST);
//        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
//        props.put("mail.smtp.ssl.trust", SMTP_HOST);
//
//        Session session = Session.getInstance(props, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
//            }
//        });
//
//        try {
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(FROM_EMAIL));
//
//            // ✅ To Recipient
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
//
//            // ✅ CC Recipient
//            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse("prashanth.kumar@dharbor.com"));
//
//            message.setSubject("Automation Test Report - Board_NR");
//
//            // ✅ Email Body
//            BodyPart textPart = new MimeBodyPart();
//            textPart.setText(" Hi Team,Please find attached the latest automation test report.Regards,Automation Bot");
//
//            // ✅ Attachment Part
//            MimeBodyPart attachmentPart = new MimeBodyPart();
//            DataSource source = new FileDataSource(reportPath);
//            attachmentPart.setDataHandler(new DataHandler(source));
//            attachmentPart.setFileName(new File(reportPath).getName());
//
//            Multipart multipart = new MimeMultipart();
//            multipart.addBodyPart(textPart);
//            multipart.addBodyPart(attachmentPart);
//
//            message.setContent(multipart);
//
//            // ✅ Send the message
//            Transport.send(message);
//
//            System.out.println("✅ Report emailed successfully to " + toEmail + " (CC: prashanth.kumar@dharbor.com)");
//
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            System.err.println("❌ Failed to send report: " + e.getMessage());
//        }
//    }
//}

package utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.*;

import java.io.File;
import java.util.Properties;

public class EmailReportSender {

    // Outlook SMTP configuration
    private static final String SMTP_HOST = "smtp.office365.com";
    private static final int SMTP_PORT = 587;

    // Sender details
    private static final String FROM_EMAIL = "chetan.patil@dharbor.com";
    private static final String FROM_PASSWORD = "Athanidhi@2025"; // ⚠️ use app password if MFA enabled

//    public static void sendReport(String toEmail, String reportPath) {
//        if (reportPath == null) {
//            System.err.println("⚠️ No report found to send.");
//            return;
//        }
//
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", SMTP_HOST);
//        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
//        props.put("mail.smtp.ssl.trust", SMTP_HOST);
//
//        // Create a mail session with authentication
//        Session session = Session.getInstance(props, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
//            }
//        });
//
//        try {
//            // Create email message
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(FROM_EMAIL));
//
//            // ✅ Primary recipient
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
//
//            // ✅ Add CC recipient
//            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse("prashanth.kumar@dharbor.com"));
//
//            message.setSubject("Automation Test Report - Issue-persistence");
//
//            // ✅ Email Body
//            BodyPart text = new MimeBodyPart();
//            text.setText("Hi Team,\n\nPlease find attached the latest automation report.\n\nRegards,\nAutomation Bot");
//
//            // ✅ Attachment
//            MimeBodyPart attachment = new MimeBodyPart();
//            DataSource source = new FileDataSource(reportPath);
//            attachment.setDataHandler(new DataHandler(source));
//            attachment.setFileName(new java.io.File(reportPath).getName());
//
//            // ✅ Combine parts
//            Multipart multipart = new MimeMultipart();
//            multipart.addBodyPart(text);
//            multipart.addBodyPart(attachment);
//            message.setContent(multipart);
//
//            // ✅ Send email
//            Transport.send(message);
//
//            System.out.println("✅ Report emailed successfully to " + toEmail +
//                    " ( attached: " + reportPath);
//
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            System.err.println("❌ Failed to send report: " + e.getMessage());
//        }
//    }
    
    // with version
    
    public static void sendReport(String toEmail, String versionText) {
        // find latest complete html report (ReportUtils default waits & has min size)
        String reportPath = ReportUtils.getLatestReportFile("target", ".html");
        if (reportPath == null) {
            System.err.println("⚠️ No HTML report found under target/. Email not sent.");
            return;
        }

        sendReportWithPath(toEmail, reportPath, versionText);
    }

    /**
     * Send a specific file path as attachment.
     */
    public static void sendReportWithPath(String toEmail, String reportPath, String versionText) {
        if (reportPath == null || !(new File(reportPath).exists())) {
            System.err.println("⚠️ Report file missing: " + reportPath);
            return;
        }
        if (FROM_PASSWORD == null || FROM_PASSWORD.isEmpty()) {
            System.err.println("⚠️ SMTP password not provided. Set OUTLOOK_PASS env var or update code.");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            // support comma-separated recipients
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            // optional CC
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse("prashanth.kumar@dharbor.com"));

            String subject = "Automation Report for Issue-Persistence with";
            if (versionText != null && !versionText.isBlank()) subject += " - Version " + versionText;
            message.setSubject(subject);

            MimeBodyPart textPart = new MimeBodyPart();
            StringBuilder body = new StringBuilder();
            body.append("Hi Team,\n\nPlease find attached the latest automation report.\n");
            if (versionText != null && !versionText.isBlank()) {
                body.append("Persistence Version: ").append(versionText).append("\n");
            }
            body.append("\nRegards,\nAutomation Bot");
            textPart.setText(body.toString());

            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(reportPath);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(new File(reportPath).getName());

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);
            message.setContent(multipart);

            Transport.send(message);
            System.out.println("📧 Email sent with attachment: " + reportPath);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
    
    
}

