package com.alejandro.microservices.orderservice.repository;

import com.alejandro.microservices.orderservice.model.Order;
import com.alejandro.microservices.orderservice.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerEmail(String customerEmail);
    List<Order> findByStatus(OrderStatus status);
    Optional<Order> findByIdAndCustomerEmail(Long id, String customerEmail);
} 