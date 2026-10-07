package com.certmonitor.service;

import com.certmonitor.model.CertificateConfig;
import com.certmonitor.repository.CertificateConfigRepository;
import com.certmonitor.repository.NotificationHistoryRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class CertificateMonitoringService {
    private static final Map<Long,String> THRESHOLDS = Map.of(
        30L,"D30", 15L,"D15", 7L,"D7", 3L,"D3", 1L,"D1", 0L,"EXPIRED"
    );

    private final CertificateConfigRepository certificateRepository;
    private final NotificationHistoryRepository historyRepository;
    private final EmailNotificationService emailService;

    public CertificateMonitoringService(CertificateConfigRepository certificateRepository,
                                        NotificationHistoryRepository historyRepository,
                                        EmailNotificationService emailService) {
        this.certificateRepository = certificateRepository;
        this.historyRepository = historyRepository;
        this.emailService = emailService;
    }

    public int processExpiryAlerts() {
        int processed = 0;
        for (CertificateConfig cert : certificateRepository.findByActiveMonitoringOrderByExpiryDateAsc("Y")) {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), cert.getExpiryDate());
            String type = days < 0 ? "EXPIRED" : THRESHOLDS.get(days);
            if (type == null) continue;

            boolean alreadySent = historyRepository.existsByCertificateIdAndNotificationTypeAndSendStatus(
                cert.getId(), type, "SENT");
            if (!alreadySent) {
                emailService.sendExpiryAlert(cert, type, days);
                processed++;
            }
        }
        return processed;
    }
}
