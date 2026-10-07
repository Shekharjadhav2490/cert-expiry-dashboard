# Office Laptop Replication Guide

This file is the manual rebuild source of truth for the Certificate Expiry Dashboard.

## Phase 0 - Prerequisites
Install/confirm:
- JDK 17
- Maven 3.9+
- Oracle Database access
- SQL Developer (recommended)
- Node.js and Angular CLI will be required when frontend work begins

No GitHub access is required for the office rebuild.

## Phase 1 - Database
Execute `database/01_tables.sql` in the target Oracle schema.

It creates:
1. CERTIFICATE_CONFIG
2. CERTIFICATE_NOTIFICATION_HISTORY
3. Supporting indexes and constraints

Important: do not store database passwords in source code.

## Phase 1 - Backend folder structure
Create:
`backend/src/main/java/com/certmonitor/`

Subfolders:
- controller
- dto
- exception
- model
- repository
- service

Also create:
`backend/src/main/resources/`

## Backend files completed so far
1. backend/pom.xml
2. backend/src/main/java/com/certmonitor/CertificateExpiryApplication.java
3. backend/src/main/resources/application.properties
4. backend/src/main/java/com/certmonitor/model/CertificateStatus.java
5. backend/src/main/java/com/certmonitor/model/CertificateConfig.java
6. backend/src/main/java/com/certmonitor/repository/CertificateConfigRepository.java
7. backend/src/main/java/com/certmonitor/dto/CertificateRequest.java
8. backend/src/main/java/com/certmonitor/dto/CertificateResponse.java
9. backend/src/main/java/com/certmonitor/service/CertificateService.java
10. backend/src/main/java/com/certmonitor/controller/CertificateController.java
11. backend/src/main/java/com/certmonitor/exception/ApiExceptionHandler.java

For office replication, copy/type the latest complete contents of each corresponding repository file.

## Environment variables
Before starting the backend, configure:
- DB_URL
- DB_USERNAME
- DB_PASSWORD

Example DB URL for a local Oracle service:
`jdbc:oracle:thin:@localhost:1521/FREEPDB1`

Use the actual office Oracle host, port and service name.

## Start backend
From the backend directory:
`mvn clean spring-boot:run`

Expected port:
`8080`

## REST APIs completed
- GET /api/certificates
- GET /api/certificates/{id}
- POST /api/certificates
- PUT /api/certificates/{id}
- DELETE /api/certificates/{id}
- PATCH /api/certificates/{id}/acknowledge

## Status calculation
Status is calculated dynamically:
- expiry before today = EXPIRED
- 0 through 30 days = CRITICAL
- greater than 30 days = SAFE

## Sample POST JSON
```json
{
  "applicationName": "Oracle WebCenter Content",
  "environmentName": "PROD",
  "certificateName": "WCC PROD SSL",
  "ownerTeam": "ECM Team",
  "emailRecipients": "ecm-team@example.com",
  "expiryDate": "2026-11-05",
  "notes": "Production certificate",
  "activeMonitoring": true
}
```

## Phase tracking
- [x] Oracle base tables
- [x] Spring Boot project foundation
- [x] JPA certificate entity/repository
- [x] CRUD REST API
- [x] Dynamic status calculation
- [x] Acknowledgement API
- [x] Notification history JPA
- [x] Email service
- [x] Scheduled expiry checker
- [x] Duplicate notification prevention
- [ ] Backend tests
- [ ] Angular frontend
- [ ] Final office deployment guide

Keep this guide synchronized after every development phase.


## Notification engine - completed
New files:
1. backend/src/main/java/com/certmonitor/model/NotificationHistory.java
2. backend/src/main/java/com/certmonitor/repository/NotificationHistoryRepository.java
3. backend/src/main/java/com/certmonitor/service/EmailNotificationService.java
4. backend/src/main/java/com/certmonitor/service/CertificateMonitoringService.java
5. backend/src/main/java/com/certmonitor/scheduler/CertificateExpiryScheduler.java
6. backend/src/main/java/com/certmonitor/controller/NotificationController.java

Additional application.properties values:
- app.mail.enabled=${MAIL_ENABLED:false}
- app.mail.from=${MAIL_FROM:no-reply@certificate-monitor.local}
- app.scheduler.certificate-check-cron=0 0 8 * * *

The cron above runs daily at 08:00 in the JVM/server local timezone.

Reminder thresholds:
- 30 days = D30
- 15 days = D15
- 7 days = D7
- 3 days = D3
- 1 day = D1
- expiry date and later = EXPIRED

Duplicate prevention:
A threshold is considered complete only when a history row exists with SEND_STATUS=SENT.
If SMTP fails, the FAILED audit row is retained and the scheduler can retry on a later eligible run.

Development safety:
Keep MAIL_ENABLED=false until SMTP is configured. This prevents accidental office/user email during development.

Notification APIs:
- GET /api/notifications/history
- POST /api/notifications/run-check

The run-check endpoint is intended for controlled testing/admin use. Add authentication before production deployment.
