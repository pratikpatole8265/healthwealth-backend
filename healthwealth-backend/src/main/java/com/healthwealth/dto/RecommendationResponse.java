package com.healthwealth.dto;

import java.math.BigDecimal;
import java.util.List;

public class RecommendationResponse {
    private Long patientId;
    private String patientName;
    private Long diseaseId;
    private String diseaseName;
    private List<Option> options;

    public RecommendationResponse(Long p, String pn, Long d, String dn, List<Option> o) {
        patientId = p;
        patientName = pn;
        diseaseId = d;
        diseaseName = dn;
        options = o;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public Long getDiseaseId() {
        return diseaseId;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public List<Option> getOptions() {
        return options;
    }

    public record Option(Long hospitalId, String hospitalName, String serviceName, int availableSlots,
                         BigDecimal estimatedCost,
                         BigDecimal insuranceContribution, BigDecimal schemeContribution,
                         BigDecimal estimatedOutOfPocket,
                         double recommendationScore, List<String> reasons) {
    }
}
