package com.certmonitor.repository;

import com.certmonitor.model.CertificateConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CertificateConfigRepository extends JpaRepository<CertificateConfig, Long> {
    List<CertificateConfig> findAllByOrderByExpiryDateAsc();
    List<CertificateConfig> findByActiveMonitoringOrderByExpiryDateAsc(String activeMonitoring);
}
