package com.certmonitor.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CertificateRequest(
    @NotBlank String applicationName,
    @NotBlank @Pattern(regexp="DEV|TEST|UAT|PROD|DR") String environmentName,
    @NotBlank String certificateName,
    @NotBlank String ownerTeam,
    @NotBlank String emailRecipients,
    @NotNull LocalDate expiryDate,
    String notes,
    @NotNull Boolean activeMonitoring
) {}
