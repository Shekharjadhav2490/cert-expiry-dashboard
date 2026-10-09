# Certificate expiry dashboard
Angular 20 frontend, Spring Boot 3.5.7 backend (Java 17 or 21), Oracle database.

## 1. Oracle setup
Use a dedicated schema with CREATE SESSION, CREATE TABLE, and a tablespace quota. Run `database/schema.sql` once while connected to that schema. Oracle 12c+ identity columns are required. No automatic DDL runs at startup.

## 2. Start the backend (Git Bash on Windows)
Install JDK 17 or 21 and Maven 3.9+. Use your actual JDK directory:
```bash
export JAVA_HOME="/c/Users/admin/.jdks/temurin-21.0.12.1"
export PATH="$JAVA_HOME/bin:$PATH"
java -version
mvn -version
cd cert-expiry-dashboard/dashboard-app/backend
export DB_URL='jdbc:oracle:thin:@//localhost:1521/FREEPDB1'
export DB_USERNAME='CERT_DASHBOARD'
read -rs -p 'Oracle password: ' DB_PASSWORD; export DB_PASSWORD
# Set only if the SMTP account requires a password:
read -rs -p 'SMTP password: ' SMTP_PASSWORD; export SMTP_PASSWORD
mvn spring-boot:run
```
Replace FREEPDB1 with your actual Oracle PDB service name. DB_URL may also use a wallet-enabled JDBC URL if your Oracle deployment requires it.

## 3. Start Angular in another terminal
Node 22.12+ (22 LTS recommended) and npm are required.
```bash
cd cert-expiry-dashboard/dashboard-app/frontend
npm install
npm start
```
Open http://localhost:4200. Angular proxies /api to localhost:8080.

## Behaviour
- Default tab lists application name, environment, expiry date, Safe/Critical status, and owner team.
- Critical means 30 days or fewer remaining; expired certificates remain Critical and show days overdue.
- Add Application validates required fields and recipient email addresses. Environments: PROD, DR, TEST, DEV, UAT.
- Email configuration stores provider label, SMTP host, port, username, sender, TLS mode, and enabled flag in Oracle. Password comes only from SMTP_PASSWORD, never the API/database.
- A daily job runs at 09:00 Asia/Kolkata. It emails each recipient separately and records successful sends to prevent repeat sends that day. Failed recipients retry on the next scheduled run.
- Save SMTP settings and enable alerts only when ready to send real emails. STARTTLS usually uses 587; SSL usually uses 465. Provider-specific SMTP/relay permissions are necessary. Microsoft OAuth/Graph authentication is not implemented in this version.
- Override APP_ZONE and ALERT_CRON as backend environment variables. For a local test use ALERT_CRON='0 */1 * * * *'; restore the daily schedule afterward.

## API
GET /api/certificates; POST /api/certificates; GET /api/email-config; PUT /api/email-config.
Example POST body:
```json
{"applicationName":"WebCenter Content","environment":"PROD","expiryDate":"2026-12-31","ownerTeam":"ECM Operations","recipients":["owner@example.com"]}
```

## Validation
`cd frontend && npm run build`
`cd backend && mvn test`

## First-version scope
This version supports listing and adding certificates and saving email settings. Editing/renewal and deletion are future work. Bind defaults to loopback and there is no login yet: add authentication and authorisation before network deployment. Run one backend scheduler instance; SMTP and database writes cannot be atomic, so a crash after sending but before recording may repeat an email. Delivery records should have a retention policy for long-running deployments. The provider label is descriptive; sending uses the configured SMTP host.
