package com.healthwealth.controller;

import com.healthwealth.service.BenefitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BenefitController {
    private final BenefitService service;

    public BenefitController(BenefitService s) {
        service = s;
    }

    @GetMapping("/schemes/eligible/{patientId}")
    public ResponseEntity<?> schemes(@PathVariable long patientId, @RequestParam long diseaseId) {
        return ResponseEntity.ok(service.schemes(patientId, diseaseId));
    }

    @GetMapping("/insurance/eligible/{patientId}")
    public ResponseEntity<?> insurance(@PathVariable long patientId, @RequestParam long diseaseId) {
        return ResponseEntity.ok(service.insurance(patientId, diseaseId));
    }
}
