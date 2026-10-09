package com.neshtek.certs;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.slf4j.LoggerFactory;
import java.time.*;
@Service
public class Alerts {
 private final Api api;private final JdbcTemplate db;private final String password;private final ZoneId zone;
 public Alerts(Api api,JdbcTemplate db,@Value("${app.smtp-password}") String password,@Value("${app.zone}") String zone){this.api=api;this.db=db;this.password=password;this.zone=ZoneId.of(zone);}
 @Scheduled(cron="${app.alert-cron}",zone="${app.zone}") public void send(){
  try {var c=api.config();if(c==null||!c.enabled())return;
   if(c.username()!=null&&!c.username().isBlank()&&password.isBlank()){LoggerFactory.getLogger(Alerts.class).warn("SMTP_PASSWORD is missing; alerts skipped");return;}
   var sender=new JavaMailSenderImpl();sender.setHost(c.host());sender.setPort(c.port());sender.setUsername(c.username());sender.setPassword(password);
   var props=sender.getJavaMailProperties();props.setProperty("mail.smtp.auth",Boolean.toString(c.username()!=null&&!c.username().isBlank()));props.setProperty("mail.smtp.starttls.enable",Boolean.toString(c.security().equals("STARTTLS")));props.setProperty("mail.smtp.starttls.required",Boolean.toString(c.security().equals("STARTTLS")));props.setProperty("mail.smtp.ssl.enable",Boolean.toString(c.security().equals("SSL")));props.setProperty("mail.smtp.ssl.checkserveridentity","true");for(String k:new String[]{"connectiontimeout","timeout","writetimeout"})props.setProperty("mail.smtp."+k,"10000");
   var day=java.sql.Date.valueOf(LocalDate.now(zone));
   for(var cert:api.certificates()){if(!cert.status().equals("Critical"))continue;
    for(var recipient:cert.recipients()){
     if(db.queryForObject("SELECT COUNT(*) FROM alert_delivery WHERE certificate_id=? AND expiry_date=? AND alert_date=? AND recipient=?",Integer.class,cert.id(),java.sql.Date.valueOf(cert.expiryDate()),day,recipient)>0)continue;
     try {var mail=new SimpleMailMessage();mail.setFrom(c.fromEmail());mail.setTo(recipient);mail.setSubject("Certificate expiry alert: "+cert.applicationName()+" ["+cert.environment()+"]");mail.setText("Application: "+cert.applicationName()+"\nEnvironment: "+cert.environment()+"\nExpiry date: "+cert.expiryDate()+"\nDays remaining: "+cert.daysRemaining()+"\nOwner team: "+cert.ownerTeam()+"\nPlease renew the certificate and update the expiry date.");sender.send(mail);db.update("INSERT INTO alert_delivery(certificate_id,expiry_date,alert_date,recipient) VALUES(?,?,?,?)",cert.id(),java.sql.Date.valueOf(cert.expiryDate()),day,recipient);}catch(Exception e){LoggerFactory.getLogger(Alerts.class).warn("Alert delivery failed for certificate {} ({})",cert.id(),e.getClass().getSimpleName());}
    }
   }
  } catch(Exception e){LoggerFactory.getLogger(Alerts.class).error("Alert job failed ({})",e.getClass().getSimpleName());}
 }
}
