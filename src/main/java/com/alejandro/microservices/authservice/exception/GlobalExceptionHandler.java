package com.alejandro.microservices.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 🛡️ Manejador Global de Excepciones - Gestión Centralizada de Errores
 * 
 * Esta clase proporciona manejo centralizado de excepciones para toda la aplicación.
 * Asegura respuestas de error consistentes en todos los endpoints y códigos HTTP
 * apropiados para diferentes tipos de errores.
 * 
 * Características principales:
 * - Formato JSON consistente de respuesta de error
 * - Códigos HTTP apropiados para cada tipo de excepción
 * - Timestamp para seguimiento y debugging de errores
 * - Mensajes de error conscientes de seguridad (sin exposición de datos sensibles)
 * - Cobertura comprehensiva de excepciones
 * 
 * Beneficios:
 * - Mejor consistencia de API
 * - Mejor manejo de errores para clientes
 * - Seguridad mejorada (sin stack traces en producción)
 * - Debugging y monitoreo más fácil
 * 
 * @author Alejandro Arango Calderón
 * @version 1.0
 * @since 2024
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 👤 Manejar conflictos de registro de usuario
     * 
     * Ocurre cuando se intenta registrar un usuario con un username existente.
     * Retorna HTTP 409 Conflict con respuesta de error estructurada.
     * 
     * @param ex UserAlreadyExistsException
     * @return ResponseEntity con detalles del error de conflicto
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.CONFLICT.value());
        error.put("error", "User already exists");
        error.put("message", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.CONFLICT); // ✅ 409 Conflict
    }

    /**
     * 🔐 Manejar fallos de autenticación
     * 
     * Ocurre cuando las credenciales de login son inválidas (username/password incorrectos).
     * Retorna HTTP 401 Unauthorized con mensaje de error genérico por seguridad.
     * 
     * @param ex BadCredentialsException
     * @return ResponseEntity con detalles del error de no autorizado
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.UNAUTHORIZED.value());
        error.put("error", "Invalid credentials");
        error.put("message", "Invalid username or password"); // 🔒 Mensaje genérico por seguridad

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED); // ✅ 401 Unauthorized
    }

    /**
     * 👤 Manejar errores de usuario no encontrado
     * 
     * Ocurre cuando se intenta acceder a un usuario que no existe en el sistema.
     * Retorna HTTP 404 Not Found con mensaje amigable para el usuario.
     * 
     * @param ex UsernameNotFoundException
     * @return ResponseEntity con detalles del error de no encontrado
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFound(UsernameNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "User not found");
        error.put("message", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // ✅ 404 Not Found
    }

    /**
     * 📝 Manejar errores de validación
     * 
     * Ocurre cuando los datos de la solicitud fallan en validación (campos requeridos faltantes, formato inválido, etc.).
     * Retorna HTTP 400 Bad Request con detalles del error de validación.
     * 
     * @param ex MethodArgumentNotValidException
     * @return ResponseEntity con detalles del error de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Validation error");
        error.put("message", "Invalid input data");

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); // ✅ 400 Bad Request
    }

    /**
     * 🚨 Manejar errores generales/inesperados
     * 
     * Manejador catch-all para cualquier excepción no manejada. Proporciona manejo
     * elegante de errores y previene fuga de información sensible.
     * Retorna HTTP 500 Internal Server Error.
     * 
     * @param ex Excepción genérica
     * @return ResponseEntity con detalles del error interno del servidor
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralError(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Internal server error");
        error.put("message", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // ✅ 500 Internal Server Error
    }
} 