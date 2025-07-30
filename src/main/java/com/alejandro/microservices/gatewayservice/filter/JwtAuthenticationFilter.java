package com.alejandro.microservices.gatewayservice.filter;

import com.alejandro.microservices.gatewayservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 🌐 JWT Authentication Filter - API Gateway Security
 * 
 * This global filter intercepts all requests to the API Gateway and validates
 * JWT tokens before routing to microservices. It's a critical security component
 * that implements centralized authentication for the entire microservices architecture.
 * 
 * Key responsibilities:
 * - Intercept all incoming HTTP requests
 * - Validate JWT tokens for protected endpoints
 * - Allow public endpoints without authentication
 * - Add user information to headers for microservices
 * - Handle authentication errors gracefully
 * 
 * Architecture benefits:
 * - Centralized security enforcement
 * - Reduced authentication overhead in microservices
 * - Consistent security policy across all services
 * - Simplified token validation logic
 * 
 * @author Alejandro Arango Calderón
 * @version 1.0
 * @since 2024
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    // 🔧 JWT utility for token validation
    private final JwtUtil jwtUtil;

    // 🌍 Public paths that don't require authentication
    // These endpoints are accessible without JWT tokens
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/auth/register",    // User registration
            "/api/auth/login",       // User authentication
            "/api/auth/health",      // Auth service health check
            "/actuator/health",      // Gateway health check
            "/actuator/info"         // Gateway information
    );

    /**
     * 🚦 Core filter method - Intercepts every request to the gateway
     * 
     * This method implements the gateway security flow:
     * 1. Check if path is public (no authentication required)
     * 2. Extract JWT token from Authorization header
     * 3. Validate token integrity and expiration
     * 4. Add user information to request headers
     * 5. Route request to appropriate microservice
     * 
     * @param exchange ServerWebExchange containing request/response
     * @param chain Gateway filter chain for processing
     * @return Mono<Void> for reactive processing
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // ⚡ Step 1: Allow public paths without authentication
        if (isPublicPath(path)) {
            log.debug("Public path accessed: {}", path);
            return chain.filter(exchange);
        }

        // 🔍 Step 2: Extract Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        // ❌ Step 3: Validate Authorization header format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 🎫 Step 4: Extract JWT token (remove "Bearer " prefix)
        String token = authHeader.substring(7);

        try {
            // ✅ Step 5: Validate JWT token
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                log.info("Valid token for user: {} accessing path: {}", username, path);
                
                // 👤 Step 6: Add user information to request headers
                // This allows microservices to access user context without re-validating tokens
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Name", username) // Custom header for user identification
                        .build();
                
                // ➡️ Step 7: Continue processing with modified request
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } else {
                // ❌ Invalid token - return 401 Unauthorized
                log.warn("Invalid token for path: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        } catch (Exception e) {
            // 🚨 Token validation error - return 401 Unauthorized
            log.error("Error validating token: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * 🔍 Check if the given path is public (no authentication required)
     * 
     * @param path Request path to check
     * @return true if path is public, false otherwise
     */
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * 📋 Filter execution order
     * 
     * Lower numbers execute first. This filter should run early in the chain
     * to ensure authentication happens before routing.
     * 
     * @return Order value (-100 for early execution)
     */
    @Override
    public int getOrder() {
        return -100; // 🚀 Execute before other filters
    }
} 