package com.skillcert.tracker.certification;

import java.time.LocalDate;

public record CertificationRequest(
        String name,
        String issuer,
        String category,
        LocalDate issueDate,
        LocalDate expiryDate,
        String credentialId,
        String description
) {
}
