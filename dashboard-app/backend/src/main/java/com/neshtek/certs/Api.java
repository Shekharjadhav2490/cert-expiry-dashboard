package com.neshtek.certs;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
@RestController @RequestMapping("/api")
public class Api {
 private final JdbcTemplate db; private final ZoneId zone;
 public Api(JdbcTemplate db,@Value("${app.zone}") String zone){this.db=db;this.zone=ZoneId.of(zone);}
 public record CertificateInput(@NotBlank @Size(max=200) String applicationName,@Pattern(regexp="PROD|DR|TEST|DEV|UAT") @NotNull String environment,@NotNull LocalDate expiryDate,@NotBlank @Size(max=200) String ownerTeam,@NotEmpty @Size(max=100) List<@NotBlank @Email @Size(max=254) String> recipients){}
 public record CertificateView(long id,String applicationName,String environment,LocalDate expiryDate,String ownerTeam,List<String> recipients,long daysRemaining,String status){}
 public static String status(LocalDate expiry,LocalDate today){return expiry.isAfter(today.plusDays(30))?"Safe":"Critical";}
 public List<CertificateView> certificates(){ LocalDate today=LocalDate.now(zone);return db.query("SELECT * FROM certificates ORDER BY expiry_date, application_name",(r,n)-> {LocalDate date=r.getDate("expiry_date").toLocalDate();return new CertificateView(r.getLong("id"),r.getString("application_name"),r.getString("environment"),date,r.getString("owner_team"),List.of(r.getString("recipients").split(",")),ChronoUnit.DAYS.between(today,date),status(date,today));});}
 @GetMapping("/certificates") public List<CertificateView> list(){return certificates();}
 @PostMapping("/certificates") @ResponseStatus(HttpStatus.CREATED) public Map<String,String> add(@Valid @RequestBody CertificateInput c){db.update("INSERT INTO certificates(application_name,environment,expiry_date,owner_team,recipients) VALUES (?,?,?,?,?)",c.applicationName().trim(),c.environment(),java.sql.Date.valueOf(c.expiryDate()),c.ownerTeam().trim(),String.join(",",c.recipients().stream().map(String::trim).distinct().toList()));return Map.of("message","Application added");}
 public record MailConfig(@NotBlank @Size(max=100) String provider,@NotBlank @Pattern(regexp="[A-Za-z0-9.-]+") @Size(max=253) String host,@Min(1) @Max(65535) int port,@Size(max=254) String username,@NotBlank @Email String fromEmail,@NotNull @Pattern(regexp="STARTTLS|SSL") String security,boolean enabled){}
 @GetMapping("/email-config") public MailConfig config(){return db.query("SELECT * FROM email_config WHERE id=1",(r,n)->new MailConfig(r.getString("provider"),r.getString("host"),r.getInt("port"),r.getString("username"),r.getString("from_email"),r.getString("security"),r.getInt("enabled")==1)).stream().findFirst().orElse(null);}
 @PutMapping("/email-config") public Map<String,String> save(@Valid @RequestBody MailConfig c){db.update("MERGE INTO email_config d USING (SELECT 1 id FROM dual) s ON(d.id=s.id) WHEN MATCHED THEN UPDATE SET provider=?,host=?,port=?,username=?,from_email=?,security=?,enabled=? WHEN NOT MATCHED THEN INSERT(id,provider,host,port,username,from_email,security,enabled) VALUES(1,?,?,?,?,?,?,?)",c.provider(),c.host(),c.port(),c.username(),c.fromEmail(),c.security(),c.enabled()?1:0,c.provider(),c.host(),c.port(),c.username(),c.fromEmail(),c.security(),c.enabled()?1:0);return Map.of("message","Email configuration saved");}
 @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class) @ResponseStatus(HttpStatus.BAD_REQUEST) public Map<String,String> validation(org.springframework.web.bind.MethodArgumentNotValidException e){return Map.of("message",e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+": "+x.getDefaultMessage()).findFirst().orElse("Invalid input"));}
 @ExceptionHandler(org.springframework.dao.DataAccessException.class) @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE) public Map<String,String> databaseError(org.springframework.dao.DataAccessException e){org.slf4j.LoggerFactory.getLogger(Api.class).error("Oracle database operation failed", e.getMostSpecificCause());return Map.of("message","Database operation failed. Check the backend log and Oracle configuration.");}
}
