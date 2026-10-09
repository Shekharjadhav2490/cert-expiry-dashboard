-- Run as the dedicated application schema (Oracle 12c or newer).
CREATE TABLE certificates (
 id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 application_name VARCHAR2(200 CHAR) NOT NULL,
 environment VARCHAR2(4 CHAR) NOT NULL CHECK(environment IN ('PROD','DR','TEST','DEV','UAT')),
 expiry_date DATE NOT NULL,
 owner_team VARCHAR2(200 CHAR) NOT NULL,
 recipients CLOB NOT NULL
);
CREATE INDEX idx_cert_expiry ON certificates(expiry_date);
CREATE TABLE email_config (
 id NUMBER PRIMARY KEY CHECK(id=1),provider VARCHAR2(100 CHAR) NOT NULL,
 host VARCHAR2(253 CHAR) NOT NULL,port NUMBER NOT NULL CHECK(port BETWEEN 1 AND 65535),
 username VARCHAR2(254 CHAR),from_email VARCHAR2(254 CHAR) NOT NULL,
 security VARCHAR2(10 CHAR) NOT NULL CHECK(security IN ('STARTTLS','SSL')),
 enabled NUMBER(1) DEFAULT 0 NOT NULL CHECK(enabled IN (0,1))
);
CREATE TABLE alert_delivery (
 certificate_id NUMBER NOT NULL REFERENCES certificates(id),expiry_date DATE NOT NULL,
 alert_date DATE NOT NULL,recipient VARCHAR2(254 CHAR) NOT NULL,
 CONSTRAINT pk_alert_delivery PRIMARY KEY(certificate_id,expiry_date,alert_date,recipient)
);
