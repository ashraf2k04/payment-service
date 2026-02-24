package com.ashraf.payment.controller;

import com.ashraf.payment.dto.*;
import com.ashraf.payment.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    public ApiResult<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResult.success("User registered successfully");
    }


    @Operation(summary = "Login and receive JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ApiResult<AuthResponse> login(
            @RequestBody AuthRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResult.success(
                authService.login(request, httpRequest)
        );
    }


    @Operation(summary = "Logout and invalidate session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/logout")
    public ApiResult<String> logout(
            @RequestHeader("Authorization") String header
    ) {

        if (!header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        String token = header.substring(7);
        authService.logout(token);

        return ApiResult.success("Logged out successfully");
    }



    @Operation(
            summary = "Refresh access token",
            description = "Generates new access and refresh tokens using refresh token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    public ApiResult<AuthResponse> refresh(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                {
                                  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                }
                                """
                            )
                    )
            )
            @RequestBody RefreshRequest request
    ) {
        return ApiResult.success(
                authService.refresh(request.refreshToken())
        );
    }


    @Operation(
            summary = "Logout from all devices",
            description = "Invalidates all active sessions for the currently authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All sessions invalidated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing access token")
    })
    @PostMapping("/logout-all")
    public ApiResult<String> logoutAll(
            @Parameter(
                    description = "Access token",
                    required = true,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader("Authorization") String header
    ) {

        if (!header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        String token = header.substring(7);

        authService.logoutAll(token);

        return ApiResult.success("All sessions logged out successfully");
    }
}