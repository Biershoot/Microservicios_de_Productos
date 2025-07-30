package com.alejandro.microservices.orderservice.dto;

import com.alejandro.microservices.orderservice.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private String customerName;
    private String customerEmail;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> orderItems;
} 