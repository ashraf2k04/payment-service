package com.ashraf.payment;

import com.ashraf.payment.dto.AuthResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullPaymentFlowTest() throws Exception {

        // Register
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "username": "ashraf",
                          "password": "password123"
                        }
                        """))
                .andExpect(status().isOk());

        // Login
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "username": "ashraf",
                          "password": "password123"
                        }
                        """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthResponse authResponse =
                objectMapper.readValue(loginResponse, AuthResponse.class);

        String token = authResponse.getToken();

        // Create Payment
        String paymentResponse = mockMvc.perform(post("/api/payments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "amount": 5000,
                          "currency": "INR",
                          "referenceId": "REF123"
                        }
                        """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String paymentId = objectMapper
                .readTree(paymentResponse)
                .get("id")
                .asText();

        // Authorize
        mockMvc.perform(post("/api/payments/" + paymentId + "/authorize")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Capture
        mockMvc.perform(post("/api/payments/" + paymentId + "/capture")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Refund
        mockMvc.perform(post("/api/payments/" + paymentId + "/refund")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Logout
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Access After Logout (Should Fail)
        mockMvc.perform(get("/api/payments/" + paymentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}