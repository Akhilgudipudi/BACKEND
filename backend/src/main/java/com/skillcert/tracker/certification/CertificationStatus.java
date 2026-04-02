package com.skillcert.tracker.certification;

public enum CertificationStatus {
    ACTIVE("active"),
    EXPIRING_SOON("expiring-soon"),
    EXPIRED("expired");

    private final String value;

    CertificationStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
