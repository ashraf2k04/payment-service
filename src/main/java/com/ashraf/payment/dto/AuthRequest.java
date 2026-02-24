package com.ashraf.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Authentication request")
public record AuthRequest(

        @Schema(example = "ashraf", description = "Username")
        String username,

        @Schema(example = "password123", description = "Password")
        String password
) {}
