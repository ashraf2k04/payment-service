package com.ashraf.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "Authentication request")
@Data
public class AuthRequest {

    @Schema(example = "ashraf", description = "Username of the user")
    private String username;

    @Schema(example = "password123", description = "User password")
    private String password;
}
