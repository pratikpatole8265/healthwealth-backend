package com.healthwealth.controller;

import com.healthwealth.service.HospitalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {
    private final HospitalService service;

    public HospitalController(HospitalService s) {
        service = s;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String city, @RequestParam(required = false) Long diseaseId) {
        return ResponseEntity.ok(service.findAll(page, size, city, diseaseId));
    }
}
