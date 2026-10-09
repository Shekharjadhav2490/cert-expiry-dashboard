package com.neshtek.certs;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
class CertificateManagementTest {
 @Test void updateTargetsExistingIdAndUsesEditedValues(){
  JdbcTemplate db=mock(JdbcTemplate.class);Api api=new Api(db,"Asia/Kolkata");
  when(db.update(anyString(),any(Object[].class))).thenReturn(1);
  var c=new Api.CertificateInput("WCC","PROD",LocalDate.of(2027,10,20),"ECM",List.of("new@example.com","team@example.com"));
  assertEquals("Application updated",api.update(7,c).get("message"));
  verify(db).update("UPDATE certificates SET application_name=?,environment=?,expiry_date=?,owner_team=?,recipients=? WHERE id=?","WCC","PROD",java.sql.Date.valueOf(c.expiryDate()),"ECM","new@example.com,team@example.com",7L);
 }
 @Test void missingUpdateReturns404(){JdbcTemplate db=mock(JdbcTemplate.class);Api api=new Api(db,"Asia/Kolkata");var c=new Api.CertificateInput("WCC","PROD",LocalDate.now(),"ECM",List.of("owner@example.com"));assertEquals(404,assertThrows(ResponseStatusException.class,()->api.update(99,c)).getStatusCode().value());}
 @Test void deletesChildHistoryBeforeParentAndRequiresTransaction() throws Exception {
  JdbcTemplate db=mock(JdbcTemplate.class);Api api=new Api(db,"Asia/Kolkata");when(db.queryForList("SELECT id FROM certificates WHERE id=? FOR UPDATE",Long.class,7L)).thenReturn(List.of(7L));
  api.delete(7L);var order=inOrder(db);order.verify(db).queryForList("SELECT id FROM certificates WHERE id=? FOR UPDATE",Long.class,7L);order.verify(db).update("DELETE FROM alert_delivery WHERE certificate_id=?",7L);order.verify(db).update("DELETE FROM certificates WHERE id=?",7L);
  assertTrue(Api.class.getMethod("delete",long.class).isAnnotationPresent(Transactional.class));
 }
 @Test void missingDeleteLeavesDatabaseUntouched(){JdbcTemplate db=mock(JdbcTemplate.class);Api api=new Api(db,"Asia/Kolkata");when(db.queryForList("SELECT id FROM certificates WHERE id=? FOR UPDATE",Long.class,99L)).thenReturn(List.of());assertEquals(404,assertThrows(ResponseStatusException.class,()->api.delete(99L)).getStatusCode().value());verify(db,never()).update(anyString(),any(Object[].class));}
}
