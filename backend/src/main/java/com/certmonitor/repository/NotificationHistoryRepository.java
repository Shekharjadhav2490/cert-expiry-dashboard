package com.certmonitor.repository;

import com.certmonitor.model.NotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
    boolean existsByCertificateIdAndNotificationTypeAndSendStatus(Long certificateId, String notificationType, String sendStatus);
    List<NotificationHistory> findAllByOrderBySentAtDesc();
}
