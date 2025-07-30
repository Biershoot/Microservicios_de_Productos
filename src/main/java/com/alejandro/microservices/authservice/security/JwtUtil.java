package com.alejandro.microservices.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 🔐 JWT Utility Class - Core Security Component
 * 
 * This class handles all JWT (JSON Web Token) operations including:
 * - Token generation with user claims and roles
 * - Token validation and expiration checking
 * - Claims extraction for authorization
 * - Secure key management using HMAC-SHA256
 * 
 * @author Alejandro Arango Calderón
 * @version 1.0
 * @since 2024
 */
@Component
public class JwtUtil {

    // 🔑 JWT Configuration - Externalized for security and flexibility
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 🛡️ Creates signing key for JWT operations
     * Uses HMAC-SHA256 algorithm for secure token signing
     * 
     * @return Key object for JWT signing
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 👤 Extracts username from JWT token
     * Used for user identification in distributed microservices
     * 
     * @param token JWT token string
     * @return Username from token subject
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * ⏰ Extracts expiration date from JWT token
     * Critical for token validation and security
     * 
     * @param token JWT token string
     * @return Expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 🔍 Generic method to extract any claim from JWT
     * Uses functional programming for flexibility
     * 
     * @param token JWT token string
     * @param claimsResolver Function to extract specific claim
     * @return Extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 🔓 Parses and validates JWT token
     * Throws exceptions for invalid tokens (handled by global exception handler)
     * 
     * @param token JWT token string
     * @return Claims object with all token data
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * ⚠️ Checks if token has expired
     * Critical security check for token validation
     * 
     * @param token JWT token string
     * @return true if expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 🎫 Generates JWT token for authenticated user
     * Creates token with user details and configurable expiration
     * 
     * @param userDetails Spring Security UserDetails object
     * @return Generated JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * 🏗️ Creates JWT token with claims and subject
     * Implements JWT standard with proper headers and payload
     * 
     * @param claims Additional claims (roles, permissions, etc.)
     * @param subject Username or user identifier
     * @return Complete JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * ✅ Validates JWT token against user details
     * Performs comprehensive validation including:
     * - Token expiration check
     * - Username matching
     * - Token integrity verification
     * 
     * @param token JWT token to validate
     * @param userDetails User details to validate against
     * @return true if valid, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
} 