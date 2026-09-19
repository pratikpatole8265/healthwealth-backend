package com.healthwealth.service;

import com.healthwealth.dao.BenefitDao;
import com.healthwealth.dao.DiseaseDao;
import org.springframework.stereotype.Service;

@Service
public class BenefitService {
    private final BenefitDao dao;
    private final PatientService patientService;
    private final DiseaseDao diseaseDao;

    public BenefitService(BenefitDao d, PatientService p, DiseaseDao dd) {
        dao = d;
        patientService = p;
        diseaseDao = dd;
    }

    public java.util.List<com.healthwealth.model.GovernmentScheme> schemes(long p, long d) {
        patientService.get(p);
        diseaseDao.findById(d);
        return dao.eligibleSchemes(p, d);
    }

    public java.util.List<com.healthwealth.model.InsurancePolicy> insurance(long p, long d) {
        patientService.get(p);
        diseaseDao.findById(d);
        return dao.eligibleInsurance(p, d);
    }
}
