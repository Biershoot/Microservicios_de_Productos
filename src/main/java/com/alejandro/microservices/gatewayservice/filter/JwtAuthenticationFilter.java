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
 * 🌐 Filtro de Autenticación JWT - Seguridad del API Gateway
 * 
 * Este filtro global intercepta todas las solicitudes al API Gateway y valida
 * tokens JWT antes de enrutar a los microservicios. Es un componente crítico
 * de seguridad que implementa autenticación centralizada para toda la arquitectura
 * de microservicios.
 * 
 * Responsabilidades principales:
 * - Interceptar todas las solicitudes HTTP entrantes
 * - Validar tokens JWT para endpoints protegidos
 * - Permitir endpoints públicos sin autenticación
 * - Agregar información del usuario a headers para microservicios
 * - Manejar errores de autenticación de forma elegante
 * 
 * Beneficios de la arquitectura:
 * - Aplicación de seguridad centralizada
 * - Reducción de overhead de autenticación en microservicios
 * - Política de seguridad consistente en todos los servicios
 * - Lógica de validación de tokens simplificada
 * 
 * @author Alejandro Arango Calderón
 * @version 1.0
 * @since 2024
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    // 🔧 Utilidad JWT para validación de tokens
    private final JwtUtil jwtUtil;

    // 🌍 Rutas públicas que no requieren autenticación
    // Estos endpoints son accesibles sin tokens JWT
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/auth/register",    // Registro de usuario
            "/api/auth/login",       // Autenticación de usuario
            "/api/auth/health",      // Health check del servicio de auth
            "/actuator/health",      // Health check del gateway
            "/actuator/info"         // Información del gateway
    );

    /**
     * 🚦 Método core del filtro - Intercepta cada solicitud al gateway
     * 
     * Este método implementa el flujo de seguridad del gateway:
     * 1. Verificar si la ruta es pública (no requiere autenticación)
     * 2. Extraer token JWT del header Authorization
     * 3. Validar integridad y expiración del token
     * 4. Agregar información del usuario a headers de la solicitud
     * 5. Enrutar solicitud al microservicio apropiado
     * 
     * @param exchange ServerWebExchange conteniendo request/response
     * @param chain Cadena de filtros del gateway para procesamiento
     * @return Mono<Void> para procesamiento reactivo
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // ⚡ Paso 1: Permitir rutas públicas sin autenticación
        if (isPublicPath(path)) {
            log.debug("Public path accessed: {}", path);
            return chain.filter(exchange);
        }

        // 🔍 Paso 2: Extraer header Authorization
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        // ❌ Paso 3: Validar formato del header Authorization
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 🎫 Paso 4: Extraer token JWT (remover prefijo "Bearer ")
        String token = authHeader.substring(7);

        try {
            // ✅ Paso 5: Validar token JWT
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                log.info("Valid token for user: {} accessing path: {}", username, path);
                
                // 👤 Paso 6: Agregar información del usuario a headers de la solicitud
                // Esto permite a los microservicios acceder al contexto del usuario sin re-validar tokens
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Name", username) // Header personalizado para identificación de usuario
                        .build();
                
                // ➡️ Paso 7: Continuar procesamiento con solicitud modificada
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } else {
                // ❌ Token inválido - retornar 401 Unauthorized
                log.warn("Invalid token for path: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        } catch (Exception e) {
            // 🚨 Error de validación de token - retornar 401 Unauthorized
            log.error("Error validating token: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * 🔍 Verificar si la ruta dada es pública (no requiere autenticación)
     * 
     * @param path Ruta de la solicitud a verificar
     * @return true si la ruta es pública, false en caso contrario
     */
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * 📋 Orden de ejecución del filtro
     * 
     * Números más bajos se ejecutan primero. Este filtro debe ejecutarse temprano
     * en la cadena para asegurar que la autenticación ocurra antes del enrutamiento.
     * 
     * @return Valor de orden (-100 para ejecución temprana)
     */
    @Override
    public int getOrder() {
        return -100; // 🚀 Ejecutar antes que otros filtros
    }
} 