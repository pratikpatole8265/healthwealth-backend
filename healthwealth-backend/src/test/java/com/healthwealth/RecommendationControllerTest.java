package com.healthwealth;

import com.healthwealth.controller.RecommendationController;
import com.healthwealth.dto.RecommendationResponse;
import com.healthwealth.service.RecommendationService;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecommendationControllerTest {

    @Test
    void shouldReturnRecommendationsForPatient() {

        // 1. Create mock service
        RecommendationService service =
                mock(RecommendationService.class);

        // 2. Create controller manually
        RecommendationController controller =
                new RecommendationController(service);

        // 3. Create expected response
        RecommendationResponse response =
                new RecommendationResponse(
                        1L,
                        "Rahul Patil",
                        1L,
                        "Diabetes",
                        List.of()
                );

        // 4. Tell mock service what to return
        when(service.recommend(1L, null))
                .thenReturn(response);

        // 5. Call controller directly
        ResponseEntity<?> result =
                controller.recommend(1L, null);

        // 6. Verify HTTP status
        assertEquals(200, result.getStatusCode().value());

        // 7. Verify response body
        assertEquals(response, result.getBody());
    }

    @Test
    void shouldReturnRecommendationsForSpecificDisease() {

        RecommendationService service =
                mock(RecommendationService.class);

        RecommendationController controller =
                new RecommendationController(service);

        RecommendationResponse response =
                new RecommendationResponse(
                        1L,
                        "Rahul Patil",
                        2L,
                        "Hypertension",
                        List.of()
                );

        when(service.recommend(1L, 2L))
                .thenReturn(response);

        ResponseEntity<?> result =
                controller.recommend(1L, 2L);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());
    }
}