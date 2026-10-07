package com.certmonitor.dto;

import com.certmonitor.model.CertificateStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CertificateResponse(
    Long id, String applicationName, String environmentName, String certificateName,
    String ownerTeam, String emailRecipients, LocalDate expiryDate, long daysRemaining,
    CertificateStatus status, String notes, boolean activeMonitoring, boolean acknowledged,
    String acknowledgedBy, LocalDateTime acknowledgedAt
) {}
