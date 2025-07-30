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
 * 🔐 Clase de Utilidades JWT - Componente Core de Seguridad
 * 
 * Esta clase maneja todas las operaciones JWT (JSON Web Token) incluyendo:
 * - Generación de tokens con claims de usuario y roles
 * - Validación de tokens y verificación de expiración
 * - Extracción de claims para autorización
 * - Gestión segura de claves usando HMAC-SHA256
 * 
 * @author Alejandro Arango Calderón
 * @version 1.0
 * @since 2024
 */
@Component
public class JwtUtil {

    // 🔑 Configuración JWT - Externalizada para seguridad y flexibilidad
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 🛡️ Crea la clave de firma para operaciones JWT
     * Usa algoritmo HMAC-SHA256 para firma segura de tokens
     * 
     * @return Objeto Key para firma JWT
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 👤 Extrae el nombre de usuario del token JWT
     * Usado para identificación de usuario en microservicios distribuidos
     * 
     * @param token String del token JWT
     * @return Nombre de usuario del subject del token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * ⏰ Extrae la fecha de expiración del token JWT
     * Crítico para validación de tokens y seguridad
     * 
     * @param token String del token JWT
     * @return Fecha de expiración
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 🔍 Método genérico para extraer cualquier claim del JWT
     * Usa programación funcional para flexibilidad
     * 
     * @param token String del token JWT
     * @param claimsResolver Función para extraer claim específico
     * @return Valor del claim extraído
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 🔓 Parsea y valida el token JWT
     * Lanza excepciones para tokens inválidos (manejadas por el global exception handler)
     * 
     * @param token String del token JWT
     * @return Objeto Claims con todos los datos del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * ⚠️ Verifica si el token ha expirado
     * Verificación crítica de seguridad para validación de tokens
     * 
     * @param token String del token JWT
     * @return true si expiró, false en caso contrario
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 🎫 Genera token JWT para usuario autenticado
     * Crea token con detalles del usuario y expiración configurable
     * 
     * @param userDetails Objeto UserDetails de Spring Security
     * @return String del token JWT generado
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * 🏗️ Crea token JWT con claims y subject
     * Implementa estándar JWT con headers y payload apropiados
     * 
     * @param claims Claims adicionales (roles, permisos, etc.)
     * @param subject Nombre de usuario o identificador de usuario
     * @return Token JWT completo
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
     * ✅ Valida token JWT contra detalles del usuario
     * Realiza validación comprehensiva incluyendo:
     * - Verificación de expiración del token
     * - Coincidencia de nombre de usuario
     * - Verificación de integridad del token
     * 
     * @param token Token JWT a validar
     * @param userDetails Detalles del usuario para validar contra
     * @return true si es válido, false en caso contrario
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
} 