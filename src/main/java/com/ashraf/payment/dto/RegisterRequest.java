package com.ashraf.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "User registration request")
@Data
public class RegisterRequest {

    @Schema(example = "ashraf")
    private String username;

    @Schema(example = "StrongPassword123")
    private String password;
}