package com.healthwealth;

import com.healthwealth.dao.BenefitDao;
import com.healthwealth.dao.DiseaseDao;
import com.healthwealth.dao.HospitalDao;
import com.healthwealth.dao.PatientDao;
import com.healthwealth.dto.RecommendationResponse;
import com.healthwealth.model.Disease;
import com.healthwealth.model.Patient;
import com.healthwealth.service.RecommendationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private PatientDao patientDao;

    @Mock
    private DiseaseDao diseaseDao;

    @Mock
    private HospitalDao hospitalDao;

    @Mock
    private BenefitDao benefitDao;

    @InjectMocks
    private RecommendationService recommendationService;

    private Patient patient;

    private Disease diabetes;

    private Disease asthma;

    @BeforeEach
    void setUp() {

        patient = new Patient();

        patient.setId(1L);
        patient.setIdentityRef("DEMO-001");
        patient.setFullName("Rahul Patil");
        patient.setDateOfBirth(LocalDate.of(1995, 5, 10));
        patient.setGender("MALE");
        patient.setCity("Pune");
        patient.setAnnualIncome(new BigDecimal("500000"));
        patient.setPhone("9999999999");
        patient.setBloodGroup("O+");
        patient.setEmergencyContact("8888888888");

        diabetes = new Disease();

        diabetes.setId(1L);
        diabetes.setName("Diabetes");
        diabetes.setCategory("Endocrine");
        diabetes.setDescription("Synthetic demo metabolic condition.");

        asthma = new Disease();

        asthma.setId(4L);
        asthma.setName("Asthma");
        asthma.setCategory("Respiratory");
        asthma.setDescription("Synthetic demo respiratory condition.");
    }

    @Test
    void shouldReturnDiabetesRecommendations() {

        when(patientDao.findById(1L))
                .thenReturn(Optional.of(patient));

        when(diseaseDao.findByPatientId(1L))
                .thenReturn(List.of(diabetes));

        RecommendationResponse response =
                recommendationService.recommend(1L, null);

        assertNotNull(response);

        assertEquals(1L, response.getPatientId());
        assertEquals("Rahul Patil", response.getPatientName());
        assertEquals(1L, response.getDiseaseId());
        assertEquals("Diabetes", response.getDiseaseName());

        assertNotNull(response.getOptions());

        verify(patientDao).findById(1L);
        verify(diseaseDao).findByPatientId(1L);

        verifyNoMoreInteractions(
                patientDao,
                diseaseDao
        );
    }

    @Test
    void shouldReturnAsthmaRecommendationsForPatient2() {

        Patient patient2 = new Patient();

        patient2.setId(2L);
        patient2.setIdentityRef("DEMO-002");
        patient2.setFullName("Demo Patient");
        patient2.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient2.setGender("MALE");
        patient2.setCity("Pune");
        patient2.setAnnualIncome(new BigDecimal("400000"));
        patient2.setPhone("9999999998");
        patient2.setBloodGroup("A+");
        patient2.setEmergencyContact("8888888887");

        when(patientDao.findById(2L))
                .thenReturn(Optional.of(patient2));

        when(diseaseDao.findByPatientId(2L))
                .thenReturn(List.of(asthma));

        RecommendationResponse response =
                recommendationService.recommend(2L, 4L);

        assertNotNull(response);

        assertEquals(2L, response.getPatientId());
        assertEquals("Demo Patient", response.getPatientName());
        assertEquals(4L, response.getDiseaseId());
        assertEquals("Asthma", response.getDiseaseName());

        assertNotNull(response.getOptions());

        verify(patientDao).findById(2L);
        verify(diseaseDao).findByPatientId(2L);

        verifyNoMoreInteractions(
                patientDao,
                diseaseDao
        );
    }

    @Test
    void shouldRejectDiseaseNotInPatientProfile() {

        when(patientDao.findById(2L))
                .thenReturn(Optional.of(patient));

        when(diseaseDao.findByPatientId(2L))
                .thenReturn(List.of(asthma));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> recommendationService.recommend(2L, 1L)
        );

        assertEquals(
                "Disease 1 is not part of patient 2's profile.",
                exception.getMessage()
        );

        verify(patientDao).findById(2L);
        verify(diseaseDao).findByPatientId(2L);
    }

    @Test
    void shouldRejectInvalidPatient() {

        when(patientDao.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> recommendationService.recommend(999L, null)
        );

        verify(patientDao).findById(999L);

        verifyNoInteractions(
                diseaseDao,
                hospitalDao,
                benefitDao
        );
    }
}