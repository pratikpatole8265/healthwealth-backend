package com.healthwealth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PatientRequest {
    @NotBlank
    @Size(max = 64)
    private String identityRef;
    @NotBlank
    @Size(max = 120)
    private String fullName;
    @NotNull
    private LocalDate dateOfBirth;
    @NotBlank
    private String gender;
    @NotBlank
    private String city;
    @NotNull
    @PositiveOrZero
    private BigDecimal annualIncome;
    private String phone;
    private String bloodGroup;
    private String emergencyContact;

    public String getIdentityRef() {
        return identityRef;
    }

    public void setIdentityRef(String v) {
        identityRef = v;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String v) {
        fullName = v;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate v) {
        dateOfBirth = v;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String v) {
        gender = v;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String v) {
        city = v;
    }

    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(BigDecimal v) {
        annualIncome = v;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String v) {
        phone = v;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String v) {
        bloodGroup = v;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String v) {
        emergencyContact = v;
    }
}
