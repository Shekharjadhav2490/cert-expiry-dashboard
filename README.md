# Certificate Expiry Dashboard

Enterprise dashboard to monitor application certificate expiry dates and send proactive notifications.

## Stack
- Backend: Java 17, Spring Boot
- Frontend: Angular
- Database: Oracle
- Notifications: Spring Mail
- Scheduling: Spring Scheduler

## Core features
1. Certificate Dashboard
   - Application
   - Environment
   - Certificate name
   - Owner team
   - Expiry date
   - Days remaining
   - Status
   - Acknowledged
   - Edit/Delete

2. Add Configuration
   - Application name
   - Environment: DEV / TEST / UAT / PROD / DR
   - Certificate name
   - Application owner team
   - Email recipients
   - Certificate expiry date
   - Notes
   - Active monitoring

3. Notification History / Audit
   - Tracks email reminders and acknowledgement activity

## Alert policy
- SAFE: more than 30 days remaining
- CRITICAL: 0 to 30 days remaining
- EXPIRED: expiry date is in the past

Planned reminder thresholds:
30, 15, 7, 3, 1 and expired day.

## Project structure

```
backend/
frontend/
database/
docs/
```

## Initial development order
1. Oracle schema
2. Spring Boot REST API
3. Scheduler and email notification engine
4. Angular dashboard
5. Notification history and acknowledgement
6. Security / RBAC
7. ServiceNow integration
