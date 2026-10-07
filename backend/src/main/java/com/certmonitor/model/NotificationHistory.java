package com.certmonitor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CERTIFICATE_NOTIFICATION_HISTORY")
public class NotificationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="CERTIFICATE_ID", nullable=false)
    private Long certificateId;
    @Column(name="NOTIFICATION_TYPE", nullable=false, length=30)
    private String notificationType;
    @Column(name="RECIPIENTS", nullable=false, length=2000)
    private String recipients;
    @Column(name="SUBJECT_LINE", length=500)
    private String subjectLine;
    @Column(name="SEND_STATUS", nullable=false, length=30)
    private String sendStatus;
    @Column(name="ERROR_MESSAGE", length=2000)
    private String errorMessage;
    @Column(name="SENT_AT", insertable=false, updatable=false)
    private LocalDateTime sentAt;

    public Long getId(){return id;}
    public Long getCertificateId(){return certificateId;} public void setCertificateId(Long v){certificateId=v;}
    public String getNotificationType(){return notificationType;} public void setNotificationType(String v){notificationType=v;}
    public String getRecipients(){return recipients;} public void setRecipients(String v){recipients=v;}
    public String getSubjectLine(){return subjectLine;} public void setSubjectLine(String v){subjectLine=v;}
    public String getSendStatus(){return sendStatus;} public void setSendStatus(String v){sendStatus=v;}
    public String getErrorMessage(){return errorMessage;} public void setErrorMessage(String v){errorMessage=v;}
    public LocalDateTime getSentAt(){return sentAt;}
}
