package com.alejandro.microservices.orderservice.controller;

import com.alejandro.microservices.orderservice.dto.CreateOrderRequest;
import com.alejandro.microservices.orderservice.dto.OrderDTO;
import com.alejandro.microservices.orderservice.model.OrderStatus;
import com.alejandro.microservices.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }
    
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderDTO createdOrder = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Order Service is running!");
    }
    
    @GetMapping("/public/health")
    public ResponseEntity<String> publicHealth() {
        return ResponseEntity.ok("Order Service Public Health Check!");
    }
} 