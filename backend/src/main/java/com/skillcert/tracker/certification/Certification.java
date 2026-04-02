package com.skillcert.tracker.certification;

import java.time.LocalDate;

public record Certification(
        String id,
        String name,
        String issuer,
        String category,
        LocalDate issueDate,
        LocalDate expiryDate,
        String credentialId,
        String description,
        String status
) {
}
