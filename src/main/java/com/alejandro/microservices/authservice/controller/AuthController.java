package com.alejandro.microservices.authservice.controller;

import com.alejandro.microservices.authservice.dto.AuthResponse;
import com.alejandro.microservices.authservice.dto.LoginRequest;
import com.alejandro.microservices.authservice.dto.RegisterRequest;
import com.alejandro.microservices.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 🌐 Authentication Controller - REST API Endpoints
 * 
 * This controller exposes REST endpoints for authentication operations.
 * It's the entry point for all authentication-related HTTP requests
 * in our microservices architecture.
 * 
 * Key endpoints:
 * - POST /api/auth/register - User registration
 * - POST /api/auth/login - User authentication
 * - GET /api/auth/health - Service health check
 * 
 * Features:
 * - CORS enabled for cross-origin requests
 * - Proper HTTP status codes
 * - JSON request/response handling
 * - Health check for monitoring
 * 
 * @author Alejandro Arango Calderón
 * @version 1.0
 * @since 2024
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 🌍 Enable CORS for frontend integration
public class AuthController {
    
    // 🔧 Service dependency injected via constructor
    private final AuthService authService;
    
    /**
     * 📝 User Registration Endpoint
     * 
     * Creates a new user account and returns a JWT token for immediate authentication.
     * This endpoint is publicly accessible (no authentication required).
     * 
     * Request body:
     * {
     *   "username": "john_doe",
     *   "password": "securePassword123",
     *   "roles": ["USER", "ADMIN"]
     * }
     * 
     * Response:
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * }
     * 
     * @param request Registration data (username, password, roles)
     * @return ResponseEntity with JWT token and HTTP 201 status
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // ✅ 201 Created
    }
    
    /**
     * 🔑 User Login Endpoint
     * 
     * Authenticates user credentials and returns a JWT token for session management.
     * This endpoint is publicly accessible (no authentication required).
     * 
     * Request body:
     * {
     *   "username": "john_doe",
     *   "password": "securePassword123"
     * }
     * 
     * Response:
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * }
     * 
     * @param request Login credentials (username, password)
     * @return ResponseEntity with JWT token and HTTP 200 status
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response); // ✅ 200 OK
    }
    
    /**
     * 💚 Health Check Endpoint
     * 
     * Simple health check for monitoring and load balancer integration.
     * Used by API Gateway and monitoring tools to verify service availability.
     * 
     * @return ResponseEntity with service status and HTTP 200 status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is running!"); // ✅ 200 OK
    }
} 