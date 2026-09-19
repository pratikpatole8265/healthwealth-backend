package com.healthwealth.controller;

import com.healthwealth.dto.PatientRequest;
import com.healthwealth.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService service;

    public PatientController(PatientService s) {
        service = s;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String city) {
        return ResponseEntity.ok(service.findAll(page, size, city));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable long id) {
        return ResponseEntity.ok(service.getResponse(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PatientRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @Valid @RequestBody PatientRequest r) {
        return ResponseEntity.ok(service.update(id, r));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/diseases")
    public ResponseEntity<?> diseases(@PathVariable long id) {
        return ResponseEntity.ok(service.diseases(id));
    }

    @PostMapping("/{patientId}/diseases/{diseaseId}")
    public ResponseEntity<Void> addDisease(@PathVariable long patientId, @PathVariable long diseaseId) {
        service.addDisease(patientId, diseaseId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
