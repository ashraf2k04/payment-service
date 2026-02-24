package com.ashraf.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "User registration request")
public record RegisterRequest(

    @Schema(example = "ashraf")
    String username,

    @Schema(example = "StrongPassword123")
     String password
){}