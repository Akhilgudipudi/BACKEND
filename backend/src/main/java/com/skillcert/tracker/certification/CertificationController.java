package com.skillcert.tracker.certification;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certifications")
public class CertificationController {

    private final CertificationService certificationService;

    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    @GetMapping
    public List<Certification> getAll() {
        return certificationService.getAll();
    }

    @GetMapping("/summary")
    public SummaryResponse getSummary() {
        return certificationService.getSummary();
    }

    @GetMapping("/{id}")
    public Certification getById(@PathVariable String id) {
        return certificationService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Certification create(@RequestBody CertificationRequest request) {
        return certificationService.create(request);
    }

    @PutMapping("/{id}")
    public Certification update(@PathVariable String id, @RequestBody CertificationRequest request) {
        return certificationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        certificationService.delete(id);
    }
}
