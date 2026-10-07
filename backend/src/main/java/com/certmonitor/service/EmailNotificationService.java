package com.certmonitor.service;

import com.certmonitor.model.CertificateConfig;
import com.certmonitor.model.NotificationHistory;
import com.certmonitor.repository.NotificationHistoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
public class EmailNotificationService {
    private final JavaMailSender mailSender;
    private final NotificationHistoryRepository historyRepository;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:no-reply@certificate-monitor.local}")
    private String fromAddress;

    public EmailNotificationService(JavaMailSender mailSender, NotificationHistoryRepository historyRepository) {
        this.mailSender = mailSender;
        this.historyRepository = historyRepository;
    }

    public void sendExpiryAlert(CertificateConfig cert, String notificationType, long daysRemaining) {
        String subject = "[Certificate Alert] " + cert.getApplicationName() + " - " + cert.getCertificateName();
        String body = buildBody(cert, daysRemaining);
        NotificationHistory history = new NotificationHistory();
        history.setCertificateId(cert.getId());
        history.setNotificationType(notificationType);
        history.setRecipients(cert.getEmailRecipients());
        history.setSubjectLine(subject);

        try {
            if (!mailEnabled) {
                history.setSendStatus("FAILED");
                history.setErrorMessage("Email sending disabled by app.mail.enabled=false");
            } else {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromAddress);
                message.setTo(parseRecipients(cert.getEmailRecipients()));
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                history.setSendStatus("SENT");
            }
        } catch (Exception ex) {
            history.setSendStatus("FAILED");
            String msg = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
            history.setErrorMessage(msg.length() > 2000 ? msg.substring(0, 2000) : msg);
        }
        historyRepository.save(history);
    }

    private String[] parseRecipients(String recipients) {
        return Arrays.stream(recipients.split("[,;]"))
            .map(String::trim).filter(s -> !s.isBlank()).toArray(String[]::new);
    }

    private String buildBody(CertificateConfig c, long daysRemaining) {
        String timing = daysRemaining < 0 ? "has expired" : "is going to expire in " + daysRemaining + " day(s)";
        return "Certificate Expiry Notification\n\n" +
            "Application: " + c.getApplicationName() + "\n" +
            "Environment: " + c.getEnvironmentName() + "\n" +
            "Certificate: " + c.getCertificateName() + "\n" +
            "Expiry Date: " + c.getExpiryDate() + "\n" +
            "Owner Team: " + c.getOwnerTeam() + "\n\n" +
            "This certificate " + timing + ".\n" +
            "Please raise a ServiceNow ticket and assign it to '" + c.getOwnerTeam() + "'.\n\n" +
            "Notes: " + (c.getNotes() == null ? "" : c.getNotes());
    }
}
