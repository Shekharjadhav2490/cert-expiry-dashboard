package com.neshtek.certs;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class InputValidationTest {
 @Test void rejectsInvalidEnvironmentAndRecipients(){try(var factory=Validation.buildDefaultValidatorFactory()) {var validator=factory.getValidator();assertTrue(validator.validate(new Api.CertificateInput("WCC","PROD",LocalDate.now(),"ECM",List.of("owner@example.com"))).isEmpty());assertFalse(validator.validate(new Api.CertificateInput("WCC","INVALID",LocalDate.now(),"ECM",List.of("bad-email"))).isEmpty());assertFalse(validator.validate(new Api.CertificateInput("WCC","PROD",LocalDate.now(),"ECM",List.of())).isEmpty());}}
}
