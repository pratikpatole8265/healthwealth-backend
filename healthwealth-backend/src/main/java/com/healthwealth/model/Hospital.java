package com.healthwealth.model;

import java.math.BigDecimal;

public class Hospital {
    private Long id;
    private String name;
    private String city;
    private String address;
    private String phone;
    private BigDecimal rating;
    private boolean emergencyAvailable;

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

    public String getCity() {
        return city;
    }

    public void setCity(String v) {
        city = v;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String v) {
        address = v;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String v) {
        phone = v;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal v) {
        rating = v;
    }

    public boolean isEmergencyAvailable() {
        return emergencyAvailable;
    }

    public void setEmergencyAvailable(boolean v) {
        emergencyAvailable = v;
    }
}
