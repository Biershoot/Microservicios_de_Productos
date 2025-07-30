package com.alejandro.microservices.orderservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Email should be valid")
    private String customerEmail;
    
    @NotEmpty(message = "Order items cannot be empty")
    private List<CreateOrderItemRequest> orderItems;
} 