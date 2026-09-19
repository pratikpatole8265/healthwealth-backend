package com.healthwealth.model;

import java.math.BigDecimal;

public class GovernmentScheme {
    private Long id;
    private String name;
    private String description;
    private String stateName;
    private Integer minAge;
    private Integer maxAge;
    private BigDecimal maxAnnualIncome;
    private BigDecimal maxCoverage;
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public String getName() {
        return name;
    }

    public void setName(String v) {
        name = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String v) {
        description = v;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String v) {
        stateName = v;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer v) {
        minAge = v;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer v) {
        maxAge = v;
    }

    public BigDecimal getMaxAnnualIncome() {
        return maxAnnualIncome;
    }

    public void setMaxAnnualIncome(BigDecimal v) {
        maxAnnualIncome = v;
    }

    public BigDecimal getMaxCoverage() {
        return maxCoverage;
    }

    public void setMaxCoverage(BigDecimal v) {
        maxCoverage = v;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean v) {
        active = v;
    }
}
