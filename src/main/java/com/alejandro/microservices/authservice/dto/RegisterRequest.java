package com.alejandro.microservices.authservice.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private Set<String> roles;
} 