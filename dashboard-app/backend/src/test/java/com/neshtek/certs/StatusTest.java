package com.neshtek.certs;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
class StatusTest {
 @Test void expiryThresholdIncludesDay30AndOverdue(){var today=LocalDate.of(2026,10,9);assertEquals("Safe",Api.status(today.plusDays(31),today));assertEquals("Critical",Api.status(today.plusDays(30),today));assertEquals("Critical",Api.status(today,today));assertEquals("Critical",Api.status(today.minusDays(1),today));}
}
