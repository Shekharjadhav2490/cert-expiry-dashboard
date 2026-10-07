# Architecture

## Runtime flow

Angular UI
  -> Spring Boot REST API
  -> Oracle Database

Spring Scheduler
  -> loads active certificates
  -> calculates days remaining
  -> determines SAFE / CRITICAL / EXPIRED
  -> checks reminder threshold
  -> sends email
  -> writes notification history

## Initial tabs

### 1. Certificate Dashboard
Shows all configured certificates with filtering and status.

### 2. Add Configuration
Creates a new certificate monitoring record.

### 3. Notification History
Provides auditability for sent/failed reminders and future acknowledgement history.

## Status rules

- SAFE: expiry date is more than 30 calendar days away
- CRITICAL: expiry date is today through 30 days away
- EXPIRED: expiry date is before today

Status should be calculated from expiry date rather than stored as a manually maintained value.

## Notification rules

Recommended reminder thresholds:
- 30 days
- 15 days
- 7 days
- 3 days
- 1 day
- expired

The notification engine must prevent duplicate reminders for the same certificate and threshold.

## Future enhancements

- Spring Security and RBAC
- SSO / LDAP
- ServiceNow ticket integration
- Teams notifications
- Dashboard KPIs
- CSV/Excel export
- Certificate owner escalation
- Configurable alert thresholds
