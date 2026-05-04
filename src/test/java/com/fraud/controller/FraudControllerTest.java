package com.fraud.controller;

import com.fraud.dto.FraudResponse;
import com.fraud.service.FraudOrchestratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FraudController.class)
class FraudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FraudOrchestratorService orchestrator;

    @Test
    void should_return_fraud_response() throws Exception {

        FraudResponse response = new FraudResponse(1L, true, List.of());

        when(orchestrator.process(org.mockito.ArgumentMatchers.any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/fraud/check")
                        .contentType("application/json")
                        .content("""
                {
                    "userId":"user1",
                    "amount":15000,
                    "location":"ZA"
                }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fraudDetected").value(true));
    }
}