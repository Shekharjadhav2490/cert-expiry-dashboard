package com.certmonitor.scheduler;

import com.certmonitor.service.CertificateMonitoringService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CertificateExpiryScheduler {
    private final CertificateMonitoringService monitoringService;

    public CertificateExpiryScheduler(CertificateMonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @Scheduled(cron = "${app.scheduler.certificate-check-cron:0 0 8 * * *}")
    public void checkCertificateExpiry() {
        monitoringService.processExpiryAlerts();
    }
}
