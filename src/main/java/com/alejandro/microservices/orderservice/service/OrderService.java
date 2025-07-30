package com.alejandro.microservices.orderservice.service;

import com.alejandro.microservices.orderservice.dto.CreateOrderRequest;
import com.alejandro.microservices.orderservice.dto.OrderDTO;
import com.alejandro.microservices.orderservice.exception.OrderNotFoundException;
import com.alejandro.microservices.orderservice.model.Order;
import com.alejandro.microservices.orderservice.model.OrderItem;
import com.alejandro.microservices.orderservice.model.OrderStatus;
import com.alejandro.microservices.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found"));
        return convertToDTO(order);
    }
    
    public OrderDTO createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setStatus(OrderStatus.PENDING);
        
        // Calculate total amount
        BigDecimal totalAmount = request.getOrderItems().stream()
                .map(item -> BigDecimal.valueOf(item.getUnitPrice()).multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);
        
        // Create order items
        List<OrderItem> orderItems = request.getOrderItems().stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProductId(item.getProductId());
                    orderItem.setProductName(item.getProductName());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setUnitPrice(BigDecimal.valueOf(item.getUnitPrice()));
                    orderItem.setTotalPrice(BigDecimal.valueOf(item.getUnitPrice()).multiply(BigDecimal.valueOf(item.getQuantity())));
                    return orderItem;
                })
                .collect(Collectors.toList());
        
        order.setOrderItems(orderItems);
        
        Order savedOrder = orderRepository.save(order);
        return convertToDTO(savedOrder);
    }
    
    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found"));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }
    
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Order with ID " + id + " not found");
        }
        orderRepository.deleteById(id);
    }
    
    public List<OrderDTO> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private OrderDTO convertToDTO(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getOrderItems().stream()
                        .map(this::convertToItemDTO)
                        .collect(Collectors.toList())
        );
    }
    
    private com.alejandro.microservices.orderservice.dto.OrderItemDTO convertToItemDTO(OrderItem item) {
        return new com.alejandro.microservices.orderservice.dto.OrderItemDTO(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
        );
    }
} 