package com.skillcert.tracker.certification;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CertificationService {

    private final Map<String, Certification> certifications = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(6);

    public CertificationService() {
        seedData().forEach(certification -> certifications.put(certification.id(), certification));
    }

    public List<Certification> getAll() {
        return certifications.values().stream()
                .sorted(Comparator.comparing(Certification::expiryDate))
                .toList();
    }

    public Certification getById(String id) {
        Certification certification = certifications.get(id);
        if (certification == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Certification not found.");
        }
        return certification;
    }

    public Certification create(CertificationRequest request) {
        validateRequest(request);
        String id = String.valueOf(sequence.incrementAndGet());
        Certification certification = toCertification(id, request);
        certifications.put(id, certification);
        return certification;
    }

    public Certification update(String id, CertificationRequest request) {
        validateRequest(request);
        if (!certifications.containsKey(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Certification not found.");
        }
        Certification certification = toCertification(id, request);
        certifications.put(id, certification);
        return certification;
    }

    public void delete(String id) {
        if (certifications.remove(id) == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Certification not found.");
        }
    }

    public SummaryResponse getSummary() {
        List<Certification> items = getAll();
        long active = items.stream().filter(item -> Objects.equals(item.status(), CertificationStatus.ACTIVE.value())).count();
        long expiringSoon = items.stream().filter(item -> Objects.equals(item.status(), CertificationStatus.EXPIRING_SOON.value())).count();
        long expired = items.stream().filter(item -> Objects.equals(item.status(), CertificationStatus.EXPIRED.value())).count();

        List<Certification> upcoming = items.stream()
                .filter(item -> !Objects.equals(item.status(), CertificationStatus.EXPIRED.value()))
                .sorted(Comparator.comparing(Certification::expiryDate))
                .limit(3)
                .toList();

        List<Certification> recent = items.stream()
                .sorted(Comparator.comparing(Certification::issueDate).reversed())
                .limit(3)
                .toList();

        List<CategoryCount> categories = items.stream()
                .collect(Collectors.groupingBy(Certification::category, TreeMap::new, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> new CategoryCount(entry.getKey(), entry.getValue()))
                .toList();

        return new SummaryResponse(items.size(), active, expiringSoon, expired, upcoming, recent, categories);
    }

    private Certification toCertification(String id, CertificationRequest request) {
        return new Certification(
                id,
                request.name().trim(),
                request.issuer().trim(),
                request.category().trim(),
                request.issueDate(),
                request.expiryDate(),
                normalizeOptional(request.credentialId()),
                normalizeOptional(request.description()),
                calculateStatus(request.expiryDate())
        );
    }

    private void validateRequest(CertificationRequest request) {
        if (request.name() == null || request.name().isBlank()
                || request.issuer() == null || request.issuer().isBlank()
                || request.category() == null || request.category().isBlank()
                || request.issueDate() == null
                || request.expiryDate() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "All required fields must be provided.");
        }

        if (request.issueDate().isAfter(request.expiryDate())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Expiry date must be after issue date.");
        }
    }

    private String calculateStatus(LocalDate expiryDate) {
        long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
        if (daysUntilExpiry < 0) {
            return CertificationStatus.EXPIRED.value();
        }
        if (daysUntilExpiry <= 30) {
            return CertificationStatus.EXPIRING_SOON.value();
        }
        return CertificationStatus.ACTIVE.value();
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private List<Certification> seedData() {
        List<CertificationRequest> requests = List.of(
                new CertificationRequest("AWS Certified Solutions Architect", "Amazon Web Services", "Technology",
                        LocalDate.parse("2023-06-15"), LocalDate.parse("2026-06-15"), "AWS-CSA-2023-5892",
                        "Professional level cloud architecture certification"),
                new CertificationRequest("Project Management Professional", "Project Management Institute", "Business",
                        LocalDate.parse("2024-01-20"), LocalDate.parse("2027-01-20"), "PMP-8472819",
                        "Industry-recognized project management certification"),
                new CertificationRequest("Certified Information Systems Security Professional", "ISC2", "Technology",
                        LocalDate.parse("2022-09-10"), LocalDate.parse("2026-04-18"), "CISSP-928471",
                        "Advanced cybersecurity certification"),
                new CertificationRequest("First Aid and CPR", "Red Cross", "Health & Safety",
                        LocalDate.parse("2024-11-05"), LocalDate.parse("2026-11-05"), "RC-FA-2024-3847",
                        "Emergency response certification"),
                new CertificationRequest("Google Analytics Certification", "Google", "Marketing",
                        LocalDate.parse("2024-08-12"), LocalDate.parse("2026-04-12"), "GA-CERT-8472",
                        "Digital analytics and reporting certification"),
                new CertificationRequest("Certified Scrum Master", "Scrum Alliance", "Business",
                        LocalDate.parse("2023-03-22"), LocalDate.parse("2026-03-22"), "CSM-2847291",
                        "Agile project management certification")
        );

        List<Certification> items = new ArrayList<>();
        long id = 0;
        for (CertificationRequest request : requests) {
            id++;
            items.add(toCertification(String.valueOf(id), request));
        }
        return items;
    }
}
