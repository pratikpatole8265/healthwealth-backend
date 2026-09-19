package com.healthwealth.model;

import java.math.BigDecimal;

public class HospitalService {
    private Long id;
    private Long hospitalId;
    private Long diseaseId;
    private String serviceName;
    private int availableSlots;
    private BigDecimal estimatedCost;

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long v) {
        hospitalId = v;
    }

    public Long getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(Long v) {
        diseaseId = v;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String v) {
        serviceName = v;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(int v) {
        availableSlots = v;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal v) {
        estimatedCost = v;
    }
}
