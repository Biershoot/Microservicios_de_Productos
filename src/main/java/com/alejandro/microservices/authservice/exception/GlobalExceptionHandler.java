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
 * 🛡️ Global Exception Handler - Centralized Error Management
 * 
 * This class provides centralized exception handling for the entire application.
 * It ensures consistent error responses across all endpoints and proper HTTP
 * status codes for different types of errors.
 * 
 * Key features:
 * - Consistent JSON error response format
 * - Proper HTTP status codes for each exception type
 * - Timestamp for error tracking and debugging
 * - Security-conscious error messages (no sensitive data exposure)
 * - Comprehensive exception coverage
 * 
 * Benefits:
 * - Improved API consistency
 * - Better error handling for clients
 * - Enhanced security (no stack traces in production)
 * - Easier debugging and monitoring
 * 
 * @author Alejandro Arango Calderón
 * @version 1.0
 * @since 2024
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 👤 Handle user registration conflicts
     * 
     * Occurs when trying to register a user with an existing username.
     * Returns HTTP 409 Conflict with structured error response.
     * 
     * @param ex UserAlreadyExistsException
     * @return ResponseEntity with conflict error details
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
     * 🔐 Handle authentication failures
     * 
     * Occurs when login credentials are invalid (wrong username/password).
     * Returns HTTP 401 Unauthorized with generic error message for security.
     * 
     * @param ex BadCredentialsException
     * @return ResponseEntity with unauthorized error details
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.UNAUTHORIZED.value());
        error.put("error", "Invalid credentials");
        error.put("message", "Invalid username or password"); // 🔒 Generic message for security

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED); // ✅ 401 Unauthorized
    }

    /**
     * 👤 Handle user not found errors
     * 
     * Occurs when trying to access a user that doesn't exist in the system.
     * Returns HTTP 404 Not Found with user-friendly message.
     * 
     * @param ex UsernameNotFoundException
     * @return ResponseEntity with not found error details
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
     * 📝 Handle validation errors
     * 
     * Occurs when request data fails validation (missing required fields, invalid format, etc.).
     * Returns HTTP 400 Bad Request with validation error details.
     * 
     * @param ex MethodArgumentNotValidException
     * @return ResponseEntity with validation error details
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
     * 🚨 Handle general/unexpected errors
     * 
     * Catch-all handler for any unhandled exceptions. Provides graceful
     * error handling and prevents sensitive information leakage.
     * Returns HTTP 500 Internal Server Error.
     * 
     * @param ex Generic Exception
     * @return ResponseEntity with internal server error details
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