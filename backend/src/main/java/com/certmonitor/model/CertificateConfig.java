package com.certmonitor.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CERTIFICATE_CONFIG")
public class CertificateConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="APPLICATION_NAME", nullable=false, length=150)
    private String applicationName;
    @Column(name="ENVIRONMENT_NAME", nullable=false, length=20)
    private String environmentName;
    @Column(name="CERTIFICATE_NAME", nullable=false, length=200)
    private String certificateName;
    @Column(name="OWNER_TEAM", nullable=false, length=150)
    private String ownerTeam;
    @Column(name="EMAIL_RECIPIENTS", nullable=false, length=2000)
    private String emailRecipients;
    @Column(name="EXPIRY_DATE", nullable=false)
    private LocalDate expiryDate;
    @Column(name="NOTES", length=2000)
    private String notes;
    @Column(name="ACTIVE_MONITORING", nullable=false, length=1)
    private String activeMonitoring = "Y";
    @Column(name="ACKNOWLEDGED", nullable=false, length=1)
    private String acknowledged = "N";
    @Column(name="ACKNOWLEDGED_BY", length=150)
    private String acknowledgedBy;
    @Column(name="ACKNOWLEDGED_AT")
    private LocalDateTime acknowledgedAt;
    @Column(name="CREATED_AT", insertable=false, updatable=false)
    private LocalDateTime createdAt;
    @Column(name="UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist @PreUpdate
    void updateTimestamp() { updatedAt = LocalDateTime.now(); }

    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getApplicationName(){return applicationName;} public void setApplicationName(String v){applicationName=v;}
    public String getEnvironmentName(){return environmentName;} public void setEnvironmentName(String v){environmentName=v;}
    public String getCertificateName(){return certificateName;} public void setCertificateName(String v){certificateName=v;}
    public String getOwnerTeam(){return ownerTeam;} public void setOwnerTeam(String v){ownerTeam=v;}
    public String getEmailRecipients(){return emailRecipients;} public void setEmailRecipients(String v){emailRecipients=v;}
    public LocalDate getExpiryDate(){return expiryDate;} public void setExpiryDate(LocalDate v){expiryDate=v;}
    public String getNotes(){return notes;} public void setNotes(String v){notes=v;}
    public String getActiveMonitoring(){return activeMonitoring;} public void setActiveMonitoring(String v){activeMonitoring=v;}
    public String getAcknowledged(){return acknowledged;} public void setAcknowledged(String v){acknowledged=v;}
    public String getAcknowledgedBy(){return acknowledgedBy;} public void setAcknowledgedBy(String v){acknowledgedBy=v;}
    public LocalDateTime getAcknowledgedAt(){return acknowledgedAt;} public void setAcknowledgedAt(LocalDateTime v){acknowledgedAt=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}
