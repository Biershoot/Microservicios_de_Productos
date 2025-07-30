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
 * 🌐 Controlador de Autenticación - Endpoints REST API
 * 
 * Este controlador expone endpoints REST para operaciones de autenticación.
 * Es el punto de entrada para todas las solicitudes HTTP relacionadas con
 * autenticación en nuestra arquitectura de microservicios.
 * 
 * Endpoints principales:
 * - POST /api/auth/register - Registro de usuario
 * - POST /api/auth/login - Autenticación de usuario
 * - GET /api/auth/health - Health check del servicio
 * 
 * Características:
 * - CORS habilitado para solicitudes cross-origin
 * - Códigos HTTP apropiados
 * - Manejo de JSON request/response
 * - Health check para monitoreo
 * 
 * @author Alejandro Arango Calderón
 * @version 1.0
 * @since 2024
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 🌍 Habilitar CORS para integración frontend
public class AuthController {
    
    // 🔧 Dependencia del servicio inyectada vía constructor
    private final AuthService authService;
    
    /**
     * 📝 Endpoint de Registro de Usuario
     * 
     * Crea una nueva cuenta de usuario y retorna un token JWT para autenticación inmediata.
     * Este endpoint es públicamente accesible (no requiere autenticación).
     * 
     * Cuerpo de la solicitud:
     * {
     *   "username": "john_doe",
     *   "password": "securePassword123",
     *   "roles": ["USER", "ADMIN"]
     * }
     * 
     * Respuesta:
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * }
     * 
     * @param request Datos de registro (username, password, roles)
     * @return ResponseEntity con token JWT y estado HTTP 201
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // ✅ 201 Created
    }
    
    /**
     * 🔑 Endpoint de Login de Usuario
     * 
     * Autentica credenciales de usuario y retorna un token JWT para gestión de sesión.
     * Este endpoint es públicamente accesible (no requiere autenticación).
     * 
     * Cuerpo de la solicitud:
     * {
     *   "username": "john_doe",
     *   "password": "securePassword123"
     * }
     * 
     * Respuesta:
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * }
     * 
     * @param request Credenciales de login (username, password)
     * @return ResponseEntity con token JWT y estado HTTP 200
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response); // ✅ 200 OK
    }
    
    /**
     * 💚 Endpoint de Health Check
     * 
     * Health check simple para monitoreo e integración con load balancer.
     * Usado por API Gateway y herramientas de monitoreo para verificar disponibilidad del servicio.
     * 
     * @return ResponseEntity con estado del servicio y estado HTTP 200
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is running!"); // ✅ 200 OK
    }
} 