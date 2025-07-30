package com.alejandro.microservices.authservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
} 