package com.ashraf.payment.controller;

import com.ashraf.payment.dto.AuthRequest;
import com.ashraf.payment.dto.AuthResponse;
import com.ashraf.payment.dto.RegisterRequest;
import com.ashraf.payment.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and session management")
public class AuthController {

    private final AuthService authService;


    @Operation(summary = "Register new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return "User registered successfully";
    }


    @Operation(summary = "Login and receive JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }


    @Operation(summary = "Logout and invalidate session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String header) {
        if (!header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        String token = header.substring(7);
        authService.logout(token);

        return "Logged out successfully";
    }
}