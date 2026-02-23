package com.ashraf.payment.controller;

import com.ashraf.payment.dto.*;
import com.ashraf.payment.entity.User;
import com.ashraf.payment.exceptions.UsernameAlreadyExistsException;
import com.ashraf.payment.repository.UserRepository;
import com.ashraf.payment.security.JwtService;
import com.ashraf.payment.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and session management")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SessionService sessionService;


    @Operation(summary = "Register new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(
                    "Username already used, try another.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    @Operation(summary = "Login and receive JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String jti = sessionService.createSession(user);

        String token = jwtService.generateToken(user.getId(), jti);

        return new AuthResponse(token);
    }

    @Operation(summary = "Logout and invalidate session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String header) {

        String token = header.substring(7);
        String jti = jwtService.extractJti(token);

        sessionService.invalidateSession(jti);

        return "Logged out successfully";
    }
}