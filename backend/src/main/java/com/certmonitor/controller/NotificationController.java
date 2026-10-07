package com.certmonitor.controller;

import com.certmonitor.model.NotificationHistory;
import com.certmonitor.repository.NotificationHistoryRepository;
import com.certmonitor.service.CertificateMonitoringService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {
    private final NotificationHistoryRepository historyRepository;
    private final CertificateMonitoringService monitoringService;

    public NotificationController(NotificationHistoryRepository historyRepository,
                                  CertificateMonitoringService monitoringService) {
        this.historyRepository = historyRepository;
        this.monitoringService = monitoringService;
    }

    @GetMapping("/history")
    public List<NotificationHistory> history() {
        return historyRepository.findAllByOrderBySentAtDesc();
    }

    @PostMapping("/run-check")
    public Map<String,Integer> runCheck() {
        return Map.of("processed", monitoringService.processExpiryAlerts());
    }
}
