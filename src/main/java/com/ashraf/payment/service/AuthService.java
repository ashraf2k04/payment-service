package com.ashraf.payment.service;


import com.ashraf.payment.dto.AuthRequest;
import com.ashraf.payment.dto.AuthResponse;
import com.ashraf.payment.dto.RegisterRequest;
import com.ashraf.payment.entity.User;
import com.ashraf.payment.entity.UserRole;
import com.ashraf.payment.exceptions.UsernameAlreadyExistsException;
import com.ashraf.payment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SessionService sessionService;

    public void register(RegisterRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new UsernameAlreadyExistsException(
                    "Username already used"
            );
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.ROLE_USER)
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(AuthRequest request) {

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid credentials")
                );

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String jti = sessionService.createSession(user);
        String token = jwtService.generateToken(user.getId(), jti);

        return new AuthResponse(token);
    }

    public void logout(String token) {
        String jti = jwtService.extractJti(token);
        sessionService.invalidateSession(jti);
    }
}
