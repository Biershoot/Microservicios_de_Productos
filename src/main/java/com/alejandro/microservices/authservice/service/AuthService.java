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
 * 🔐 Servicio de Autenticación - Lógica de Negocio Core
 * 
 * Este servicio maneja todas las operaciones de autenticación incluyendo registro
 * de usuarios, login y generación de tokens JWT. Es el corazón del sistema de
 * autenticación en nuestra arquitectura de microservicios.
 * 
 * Responsabilidades principales:
 * - Registro de usuarios con asignación de roles
 * - Codificación segura de contraseñas usando BCrypt
 * - Autenticación y validación de usuarios
 * - Generación de tokens JWT para sesiones sin estado
 * - Aplicación de reglas de negocio (nombres de usuario únicos, etc.)
 * 
 * @author Alejandro Arango Calderón
 * @version 1.0
 * @since 2024
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    // 🔧 Dependencias inyectadas vía constructor (Lombok @RequiredArgsConstructor)
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Implementación BCrypt
    private final JwtUtil jwtUtil; // Operaciones de tokens JWT
    private final AuthenticationManager authenticationManager; // Autenticación Spring Security
    
    /**
     * 📝 Registro de Usuario - Crea nueva cuenta de usuario
     * 
     * Este método implementa el flujo de registro de usuario:
     * 1. Validar unicidad del nombre de usuario
     * 2. Codificar contraseña de forma segura con BCrypt
     * 3. Crear usuario con roles por defecto
     * 4. Generar token JWT para autenticación inmediata
     * 
     * @param request Solicitud de registro con username, password y roles
     * @return AuthResponse conteniendo token JWT
     * @throws UserAlreadyExistsException si el username ya existe
     */
    public AuthResponse register(RegisterRequest request) {
        
        // 🔍 Paso 1: Verificar si el username ya existe
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        
        // 🏗️ Paso 2: Crear usuario con contraseña codificada y roles
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // 🔒 Codificación segura de contraseña
                .roles(request.getRoles() != null ? request.getRoles() : Set.of("USER")) // 👤 Asignación de rol por defecto
                .build();
        
        // 💾 Paso 3: Guardar usuario en base de datos
        User savedUser = userRepository.save(user);
        
        // 🎫 Paso 4: Generar token JWT para autenticación inmediata
        String token = jwtUtil.generateToken(savedUser);
        
        return new AuthResponse(token);
    }
    
    /**
     * 🔑 Login de Usuario - Autentica usuario existente
     * 
     * Este método implementa el flujo de login:
     * 1. Autenticar credenciales usando Spring Security
     * 2. Cargar detalles del usuario desde base de datos
     * 3. Generar token JWT para la sesión
     * 
     * @param request Solicitud de login con username y password
     * @return AuthResponse conteniendo token JWT
     * @throws RuntimeException si la autenticación falla o el usuario no se encuentra
     */
    public AuthResponse login(LoginRequest request) {
        
        // 🔐 Paso 1: Autenticar credenciales del usuario
        // Esto valida username/password contra datos almacenados
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        // 👤 Paso 2: Cargar detalles completos del usuario desde base de datos
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 🎫 Paso 3: Generar token JWT para sesión autenticada
        String token = jwtUtil.generateToken(user);
        
        return new AuthResponse(token);
    }
} 