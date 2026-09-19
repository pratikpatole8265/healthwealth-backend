package com.healthwealth.service;

import com.healthwealth.dao.DiseaseDao;
import com.healthwealth.dao.PatientDao;
import com.healthwealth.dto.PagedResponse;
import com.healthwealth.dto.PatientRequest;
import com.healthwealth.dto.PatientResponse;
import com.healthwealth.exception.InvalidInputException;
import com.healthwealth.exception.ResourceNotFoundException;
import com.healthwealth.model.Patient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PatientService {
    private final PatientDao patientDao;
    private final DiseaseDao diseaseDao;

    public PatientService(PatientDao p, DiseaseDao d) {
        patientDao = p;
        diseaseDao = d;
    }

    public PagedResponse<PatientResponse> findAll(int page, int size, String city) {
        var content = patientDao.findAll(page, size, city).stream().map(this::toResponse).toList();
        long total = patientDao.count(city);
        return new PagedResponse<>(content, page, size, (int) Math.ceil((double) total / size), total);
    }

    public Patient get(long id) {
        return patientDao.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    public PatientResponse getResponse(long id) {
        return toResponse(get(id));
    }

    public PatientResponse create(PatientRequest r) {
        validate(r);
        Patient p = map(r);
        long id = patientDao.insert(p);
        return toResponse(get(id));
    }

    public PatientResponse update(long id, PatientRequest r) {
        validate(r);
        patientDao.update(id, map(r));
        return toResponse(get(id));
    }

    public void delete(long id) {
        patientDao.delete(id);
    }

    public java.util.List<?> diseases(long id) {
        get(id);
        return diseaseDao.findByPatientId(id);
    }

    public void addDisease(long patientId, long diseaseId) {
        get(patientId);
        diseaseDao.findById(diseaseId);
        diseaseDao.addToPatient(patientId, diseaseId);
    }

    private void validate(PatientRequest r) {
        if (r.getDateOfBirth().isAfter(LocalDate.now()))
            throw new InvalidInputException("dateOfBirth cannot be in the future.");
    }

    private Patient map(PatientRequest r) {
        Patient p = new Patient();
        p.setIdentityRef(r.getIdentityRef());
        p.setFullName(r.getFullName());
        p.setDateOfBirth(r.getDateOfBirth());
        p.setGender(r.getGender());
        p.setCity(r.getCity());
        p.setAnnualIncome(r.getAnnualIncome());
        p.setPhone(r.getPhone());
        p.setBloodGroup(r.getBloodGroup());
        p.setEmergencyContact(r.getEmergencyContact());
        return p;
    }

    private PatientResponse toResponse(Patient p) {
        PatientResponse r = new PatientResponse();
        r.setId(p.getId());
        r.setIdentityRef(p.getIdentityRef());
        r.setFullName(p.getFullName());
        r.setDateOfBirth(p.getDateOfBirth());
        r.setGender(p.getGender());
        r.setCity(p.getCity());
        r.setAnnualIncome(p.getAnnualIncome());
        r.setPhone(p.getPhone());
        r.setBloodGroup(p.getBloodGroup());
        r.setEmergencyContact(p.getEmergencyContact());
        return r;
    }
}
