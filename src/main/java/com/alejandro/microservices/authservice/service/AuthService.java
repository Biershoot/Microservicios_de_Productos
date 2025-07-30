package com.alejandro.microservices.authservice.service;

import com.alejandro.microservices.authservice.dto.AuthResponse;
import com.alejandro.microservices.authservice.dto.LoginRequest;
import com.alejandro.microservices.authservice.dto.RegisterRequest;
import com.alejandro.microservices.authservice.exception.UserAlreadyExistsException;
import com.alejandro.microservices.authservice.model.User;
import com.alejandro.microservices.authservice.repository.UserRepository;
import com.alejandro.microservices.authservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(request.getRoles() != null ? request.getRoles() : Set.of("USER"))
                .build();
        
        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser);
        
        return new AuthResponse(token);
    }
    
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String token = jwtUtil.generateToken(user);
        
        return new AuthResponse(token);
    }
} 