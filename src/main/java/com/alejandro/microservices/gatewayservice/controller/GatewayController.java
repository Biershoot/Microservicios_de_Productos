package com.alejandro.microservices.gatewayservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gateway")
public class GatewayController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Gateway Service is running!");
    }

    @GetMapping("/info")
    public ResponseEntity<String> info() {
        return ResponseEntity.ok("API Gateway - Routes to auth-service, product-service, and order-service");
    }
} 