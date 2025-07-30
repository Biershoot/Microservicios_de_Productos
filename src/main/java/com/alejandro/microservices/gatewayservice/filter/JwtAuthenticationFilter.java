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

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    // Rutas públicas que no requieren autenticación
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/health",
            "/actuator/health",
            "/actuator/info"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // Permitir rutas públicas
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // Extraer token del header Authorization
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            // Validar token
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                log.info("Valid token for user: {} accessing path: {}", username, path);
                
                // Agregar información del usuario al header para que los microservicios puedan usarla
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Name", username)
                        .build();
                
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } else {
                log.warn("Invalid token for path: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        return -100; // Ejecutar antes que otros filtros
    }
} 