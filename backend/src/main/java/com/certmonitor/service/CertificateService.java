package com.certmonitor.service;

import com.certmonitor.dto.*;
import com.certmonitor.model.*;
import com.certmonitor.repository.CertificateConfigRepository;
import org.springframework.stereotype.Service;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CertificateService {
    private final CertificateConfigRepository repository;

    public CertificateService(CertificateConfigRepository repository) { this.repository = repository; }

    public List<CertificateResponse> findAll() {
        return repository.findAllByOrderByExpiryDateAsc().stream().map(this::toResponse).toList();
    }

    public CertificateResponse findById(Long id) { return toResponse(getEntity(id)); }

    public CertificateResponse create(CertificateRequest request) {
        CertificateConfig c = new CertificateConfig();
        apply(c, request);
        return toResponse(repository.save(c));
    }

    public CertificateResponse update(Long id, CertificateRequest request) {
        CertificateConfig c = getEntity(id);
        apply(c, request);
        return toResponse(repository.save(c));
    }

    public void delete(Long id) { repository.delete(getEntity(id)); }

    public CertificateResponse acknowledge(Long id, String acknowledgedBy) {
        CertificateConfig c = getEntity(id);
        c.setAcknowledged("Y");
        c.setAcknowledgedBy((acknowledgedBy == null || acknowledgedBy.isBlank()) ? "Dashboard User" : acknowledgedBy.trim());
        c.setAcknowledgedAt(LocalDateTime.now());
        return toResponse(repository.save(c));
    }

    public CertificateStatus status(LocalDate expiryDate) {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
        if (days < 0) return CertificateStatus.EXPIRED;
        if (days <= 30) return CertificateStatus.CRITICAL;
        return CertificateStatus.SAFE;
    }

    private CertificateConfig getEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Certificate not found: " + id));
    }

    private void apply(CertificateConfig c, CertificateRequest r) {
        c.setApplicationName(r.applicationName().trim());
        c.setEnvironmentName(r.environmentName());
        c.setCertificateName(r.certificateName().trim());
        c.setOwnerTeam(r.ownerTeam().trim());
        c.setEmailRecipients(r.emailRecipients().trim());
        c.setExpiryDate(r.expiryDate());
        c.setNotes(r.notes());
        c.setActiveMonitoring(Boolean.TRUE.equals(r.activeMonitoring()) ? "Y" : "N");
    }

    private CertificateResponse toResponse(CertificateConfig c) {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), c.getExpiryDate());
        return new CertificateResponse(c.getId(), c.getApplicationName(), c.getEnvironmentName(),
            c.getCertificateName(), c.getOwnerTeam(), c.getEmailRecipients(), c.getExpiryDate(), days,
            status(c.getExpiryDate()), c.getNotes(), "Y".equals(c.getActiveMonitoring()),
            "Y".equals(c.getAcknowledged()), c.getAcknowledgedBy(), c.getAcknowledgedAt());
    }
}
