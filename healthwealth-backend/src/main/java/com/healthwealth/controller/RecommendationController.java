package com.healthwealth.controller;

import com.healthwealth.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    /*
     * GET /api/recommendations/{patientId}
     *
     * Example:
     * GET http://localhost:8080/api/recommendations/2
     *
     * If diseaseId is NOT provided:
     * -> Automatically uses the patient's first disease.
     *
     * Example:
     * Patient 2 has Asthma (diseaseId = 4)
     * -> /api/recommendations/2
     * -> recommends Asthma
     */

    @GetMapping("/{patientId}")
    public ResponseEntity<?> recommend(
            @PathVariable long patientId,
            @RequestParam(required = false) Long diseaseId) {

        return ResponseEntity.ok(
                service.recommend(patientId, diseaseId)
        );
    }
}