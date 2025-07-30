package com.alejandro.microservices.orderservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OrderServiceUserDetailsService implements UserDetailsService {
    
    private final JwtUtil jwtUtil;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Para el order-service, creamos un UserDetails básico
        // En un escenario real, podrías hacer una llamada al auth-service para obtener detalles del usuario
        return User.builder()
                .username(username)
                .password("") // No necesitamos password para validación JWT
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
} 