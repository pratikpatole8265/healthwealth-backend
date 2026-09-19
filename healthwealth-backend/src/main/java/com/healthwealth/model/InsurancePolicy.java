package com.healthwealth.model;

import java.math.BigDecimal;

public class InsurancePolicy {
    private Long id;
    private String providerName;
    private String policyName;
    private BigDecimal maxCoverage;
    private BigDecimal coveragePercent;
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String v) {
        providerName = v;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String v) {
        policyName = v;
    }

    public BigDecimal getMaxCoverage() {
        return maxCoverage;
    }

    public void setMaxCoverage(BigDecimal v) {
        maxCoverage = v;
    }

    public BigDecimal getCoveragePercent() {
        return coveragePercent;
    }

    public void setCoveragePercent(BigDecimal v) {
        coveragePercent = v;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean v) {
        active = v;
    }
}
