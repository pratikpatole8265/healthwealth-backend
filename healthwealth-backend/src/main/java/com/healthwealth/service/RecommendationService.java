package com.healthwealth.service;

import com.healthwealth.dao.BenefitDao;
import com.healthwealth.dao.DiseaseDao;
import com.healthwealth.dao.HospitalDao;
import com.healthwealth.dao.PatientDao;

import com.healthwealth.dto.RecommendationResponse;

import com.healthwealth.exception.InvalidInputException;
import com.healthwealth.exception.ResourceNotFoundException;

import com.healthwealth.model.Disease;
import com.healthwealth.model.GovernmentScheme;
import com.healthwealth.model.Hospital;
import com.healthwealth.model.InsurancePolicy;
import com.healthwealth.model.Patient;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RecommendationService {

    private final PatientDao patientDao;
    private final DiseaseDao diseaseDao;
    private final HospitalDao hospitalDao;
    private final BenefitDao benefitDao;

    public RecommendationService(
            PatientDao patientDao,
            DiseaseDao diseaseDao,
            HospitalDao hospitalDao,
            BenefitDao benefitDao) {

        this.patientDao = patientDao;
        this.diseaseDao = diseaseDao;
        this.hospitalDao = hospitalDao;
        this.benefitDao = benefitDao;
    }

    public RecommendationResponse recommend(
            long patientId,
            Long requestedDiseaseId) {

        // =====================================================
        // 1. FIND PATIENT
        // =====================================================

        Patient patient = patientDao.findById(patientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found with id: " + patientId
                        )
                );


        // =====================================================
        // 2. FIND DISEASES BELONGING TO THIS PATIENT
        // =====================================================

        List<Disease> patientDiseases =
                diseaseDao.findByPatientId(patientId);

        if (patientDiseases == null || patientDiseases.isEmpty()) {

            throw new InvalidInputException(
                    "Patient has no diseases in demo profile."
            );
        }


        // =====================================================
        // 3. SELECT DISEASE
        // =====================================================

        Disease selectedDisease;


        /*
         * CASE 1:
         *
         * No diseaseId supplied.
         *
         * Example:
         *
         * GET /api/recommendations/2
         *
         * Patient 2 has:
         *
         * diseaseId = 4
         * diseaseName = Asthma
         *
         * Therefore disease 4 will be selected.
         */

        if (requestedDiseaseId == null) {

            selectedDisease = patientDiseases.get(0);

        }

        /*
         * CASE 2:
         *
         * diseaseId supplied.
         *
         * Example:
         *
         * GET /api/recommendations/2?diseaseId=4
         *
         * We verify that disease 4 actually belongs
         * to patient 2.
         */

        else {

            selectedDisease = patientDiseases.stream()
                    .filter(disease ->
                            disease.getId() != null
                                    && disease.getId()
                                    .longValue()
                                    == requestedDiseaseId.longValue()
                    )
                    .findFirst()
                    .orElseThrow(() ->
                            new InvalidInputException(
                                    "Disease " + requestedDiseaseId
                                            + " is not part of patient "
                                            + patientId
                                            + "'s profile."
                            )
                    );
        }


        // =====================================================
        // 4. GET ELIGIBLE GOVERNMENT SCHEMES
        // =====================================================

        List<GovernmentScheme> schemes =
                benefitDao.eligibleSchemes(
                        patientId,
                        selectedDisease.getId()
                );


        // =====================================================
        // 5. GET ELIGIBLE INSURANCE POLICIES
        // =====================================================

        List<InsurancePolicy> policies =
                benefitDao.eligibleInsurance(
                        patientId,
                        selectedDisease.getId()
                );


        // =====================================================
        // 6. FIND BEST GOVERNMENT SCHEME COVERAGE
        // =====================================================

        BigDecimal bestScheme =
                schemes.stream()
                        .map(GovernmentScheme::getMaxCoverage)
                        .filter(value -> value != null)
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);


        // =====================================================
        // 7. FIND BEST INSURANCE COVERAGE %
        // =====================================================

        BigDecimal insurancePercent =
                policies.stream()
                        .map(InsurancePolicy::getCoveragePercent)
                        .filter(value -> value != null)
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);


        // =====================================================
        // 8. FIND HOSPITAL SERVICES FOR SELECTED DISEASE
        // =====================================================

        List<com.healthwealth.model.HospitalService> hospitalServices =
                hospitalDao.servicesForDisease(
                        selectedDisease.getId()
                );


        List<RecommendationResponse.Option> options =
                new ArrayList<>();


        // =====================================================
        // 9. CREATE RECOMMENDATION FOR EACH HOSPITAL
        // =====================================================

        for (com.healthwealth.model.HospitalService hospitalService
                : hospitalServices) {


            // =================================================
            // 10. FIND HOSPITAL
            // =================================================

            Hospital hospital =
                    hospitalDao.findById(
                            hospitalService.getHospitalId()
                    );


            // =================================================
            // 11. ESTIMATED COST
            // =================================================

            BigDecimal estimatedCost =
                    hospitalService.getEstimatedCost();


            // =================================================
            // 12. INSURANCE CONTRIBUTION
            // =================================================

            BigDecimal insuranceAmount =
                    estimatedCost
                            .multiply(insurancePercent)
                            .divide(
                                    BigDecimal.valueOf(100),
                                    2,
                                    RoundingMode.HALF_UP
                            );


            // =================================================
            // 13. GOVERNMENT SCHEME CONTRIBUTION
            // =================================================

            BigDecimal schemeAmount =
                    estimatedCost.min(bestScheme);


            // =================================================
            // 14. OUT-OF-POCKET COST
            // =================================================

            BigDecimal outOfPocket =
                    estimatedCost
                            .subtract(
                                    insuranceAmount
                                            .add(schemeAmount)
                            );


            if (outOfPocket.signum() < 0) {

                outOfPocket =
                        BigDecimal.ZERO.setScale(2);
            }


            // =================================================
            // 15. RECOMMENDATION SCORE
            // =================================================

            double score = 0;

            List<String> reasons =
                    new ArrayList<>();


            // -----------------------------------------------
            // Availability
            // -----------------------------------------------

            if (hospitalService.getAvailableSlots() > 0) {

                score += 30;

                reasons.add(
                        "Service has demo availability."
                );
            }


            // -----------------------------------------------
            // Same city
            // -----------------------------------------------

            if (hospital.getCity() != null
                    && patient.getCity() != null
                    && hospital.getCity()
                    .equalsIgnoreCase(patient.getCity())) {

                score += 20;

                reasons.add(
                        "Hospital is in the patient's city."
                );
            }


            // -----------------------------------------------
            // Hospital rating
            // -----------------------------------------------

            if (hospital.getRating() != null) {

                score +=
                        hospital.getRating().doubleValue() * 6;
            }


            // -----------------------------------------------
            // Emergency availability
            // -----------------------------------------------

            if (hospital.isEmergencyAvailable()) {

                score += 10;

                reasons.add(
                        "Emergency service is available."
                );
            }


            // -----------------------------------------------
            // Financial assistance
            // -----------------------------------------------

            if (outOfPocket.compareTo(estimatedCost) < 0) {

                score += 10;

                reasons.add(
                        "Demo insurance/government support "
                                + "reduces the estimated "
                                + "out-of-pocket amount."
                );
            }


            // =================================================
            // 16. ADD RECOMMENDATION OPTION
            // =================================================

            options.add(
                    new RecommendationResponse.Option(

                            hospital.getId(),

                            hospital.getName(),

                            hospitalService.getServiceName(),

                            hospitalService.getAvailableSlots(),

                            estimatedCost,

                            insuranceAmount,

                            schemeAmount,

                            outOfPocket,

                            Math.round(
                                    Math.min(score, 100)
                                            * 100.0
                            ) / 100.0,

                            reasons
                    )
            );
        }


        // =====================================================
        // 17. SORT RECOMMENDATIONS
        // =====================================================

        options.sort(
                Comparator
                        .comparingDouble(
                                RecommendationResponse.Option
                                        ::recommendationScore
                        )
                        .reversed()
                        .thenComparing(
                                RecommendationResponse.Option
                                        ::estimatedOutOfPocket
                        )
        );


        // =====================================================
        // 18. RETURN FINAL RESPONSE
        // =====================================================

        return new RecommendationResponse(

                patient.getId(),

                patient.getFullName(),

                selectedDisease.getId(),

                selectedDisease.getName(),

                options
        );
    }
}