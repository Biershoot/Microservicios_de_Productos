package com.alejandro.microservices.authservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 🔐 JWT Authentication Filter - Spring Security Integration
 * 
 * This filter intercepts all HTTP requests to validate JWT tokens and establish
 * authentication context. It's a critical component for securing microservices
 * by implementing stateless authentication.
 * 
 * Key responsibilities:
 * - Extract JWT from Authorization header
 * - Validate token integrity and expiration
 * - Load user details and establish security context
 * - Handle authentication for stateless microservices
 * 
 * @author Alejandro Arango Calderón
 * @version 1.0
 * @since 2024
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    // 🔧 Dependencies injected via constructor (Lombok @RequiredArgsConstructor)
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    
    /**
     * 🚦 Core filter method - Intercepts every HTTP request
     * 
     * This method implements the JWT authentication flow:
     * 1. Extract Authorization header
     * 2. Validate Bearer token format
     * 3. Parse and validate JWT
     * 4. Load user details
     * 5. Establish Spring Security context
     * 
     * @param request HTTP request object
     * @param response HTTP response object
     * @param filterChain Spring filter chain
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 🔍 Step 1: Extract Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        
        // ⚡ Early return if no valid Authorization header
        // This optimizes performance by avoiding unnecessary processing
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 🎫 Step 2: Extract JWT token (remove "Bearer " prefix)
        jwt = authHeader.substring(7);
        
        // 👤 Step 3: Extract username from JWT
        username = jwtUtil.extractUsername(jwt);
        
        // 🔒 Step 4: Validate token and establish authentication context
        // Only process if username exists and no authentication is already set
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 📋 Load user details from database/cache
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            
            // ✅ Validate JWT token against user details
            if (jwtUtil.validateToken(jwt, userDetails)) {
                
                // 🏗️ Create authentication token with user details and authorities
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // No credentials needed for JWT
                        userDetails.getAuthorities() // User roles and permissions
                );
                
                // 🔗 Set additional authentication details (IP, session, etc.)
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // 🎯 Establish authentication context for the request
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // ➡️ Continue with the filter chain
        filterChain.doFilter(request, response);
    }
} 