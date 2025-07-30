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

/**
 * 🔐 Authentication Service - Core Business Logic
 * 
 * This service handles all authentication operations including user registration,
 * login, and JWT token generation. It's the heart of the authentication system
 * in our microservices architecture.
 * 
 * Key responsibilities:
 * - User registration with role assignment
 * - Secure password encoding using BCrypt
 * - User authentication and validation
 * - JWT token generation for stateless sessions
 * - Business rule enforcement (unique usernames, etc.)
 * 
 * @author Alejandro Arango Calderón
 * @version 1.0
 * @since 2024
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    // 🔧 Dependencies injected via constructor (Lombok @RequiredArgsConstructor)
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // BCrypt implementation
    private final JwtUtil jwtUtil; // JWT token operations
    private final AuthenticationManager authenticationManager; // Spring Security auth
    
    /**
     * 📝 User Registration - Creates new user account
     * 
     * This method implements the user registration flow:
     * 1. Validate username uniqueness
     * 2. Encode password securely with BCrypt
     * 3. Create user with default roles
     * 4. Generate JWT token for immediate authentication
     * 
     * @param request Registration request with username, password, and roles
     * @return AuthResponse containing JWT token
     * @throws UserAlreadyExistsException if username already exists
     */
    public AuthResponse register(RegisterRequest request) {
        
        // 🔍 Step 1: Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        
        // 🏗️ Step 2: Create user with encoded password and roles
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // 🔒 Secure password encoding
                .roles(request.getRoles() != null ? request.getRoles() : Set.of("USER")) // 👤 Default role assignment
                .build();
        
        // 💾 Step 3: Save user to database
        User savedUser = userRepository.save(user);
        
        // 🎫 Step 4: Generate JWT token for immediate authentication
        String token = jwtUtil.generateToken(savedUser);
        
        return new AuthResponse(token);
    }
    
    /**
     * 🔑 User Login - Authenticates existing user
     * 
     * This method implements the login flow:
     * 1. Authenticate credentials using Spring Security
     * 2. Load user details from database
     * 3. Generate JWT token for session
     * 
     * @param request Login request with username and password
     * @return AuthResponse containing JWT token
     * @throws RuntimeException if authentication fails or user not found
     */
    public AuthResponse login(LoginRequest request) {
        
        // 🔐 Step 1: Authenticate user credentials
        // This validates username/password against stored data
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        // 👤 Step 2: Load complete user details from database
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 🎫 Step 3: Generate JWT token for authenticated session
        String token = jwtUtil.generateToken(user);
        
        return new AuthResponse(token);
    }
} 