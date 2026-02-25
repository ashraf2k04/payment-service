package com.ashraf.payment;

import com.ashraf.payment.dto.AuthResponse;
import com.fasterxml.jackson.databind.JsonNode;
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

        String username = "user_" + System.currentTimeMillis();

        // 1️⃣ Register
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "username": "%s",
                          "password": "password123"
                        }
                        """.formatted(username)))
                .andExpect(status().isOk());

        // 2️⃣ Login
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "username": "%s",
                          "password": "password123"
                        }
                        """.formatted(username)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract token from ApiResult<AuthResponse>
        JsonNode loginJson = objectMapper.readTree(loginResponse);
        String token = loginJson.get("data").get("accesstoken").asText();

        // 3️⃣ Create Payment
        String paymentResponse = mockMvc.perform(post("/api/payments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "amount": 5000,
                          "currency": "INR",
                          "referenceId": "REF-%d"
                        }
                        """.formatted(System.currentTimeMillis())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String paymentId = objectMapper
                .readTree(paymentResponse)
                .get("data")
                .get("id")
                .asText();

        // 4️⃣ Authorize
        mockMvc.perform(post("/api/payments/" + paymentId + "/authorize")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 5️⃣ Capture
        mockMvc.perform(post("/api/payments/" + paymentId + "/capture")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 6️⃣ Logout
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 7️⃣ Access After Logout → Should Fail (stateful JWT)
        mockMvc.perform(get("/api/payments/" + paymentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}
