package com.tesfaye.userservice.service;

import com.tesfaye.userservice.dto.AuthResponse;
import com.tesfaye.userservice.dto.LoginRequest;
import com.tesfaye.userservice.dto.RegisterRequest;
import com.tesfaye.userservice.exception.UserAlreadyExistsException;
import com.tesfaye.userservice.model.User;
import com.tesfaye.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                "User already exists with email: " + request.getEmail());
        }

        // Build and save user
        User user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role("USER")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        // Generate JWT token
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
            .token(token)
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole())
            .message("Registration successful")
            .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticate credentials
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        // Load user and generate token
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);
        log.info("User logged in: {}", user.getEmail());

        return AuthResponse.builder()
            .token(token)
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole())
            .message("Login successful")
            .build();
    }
}