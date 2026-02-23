package com.ashraf.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "Authentication response containing JWT token")
@Data
@AllArgsConstructor
public class AuthResponse {

    @Schema(
            description = "JWT access token",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String token;
}