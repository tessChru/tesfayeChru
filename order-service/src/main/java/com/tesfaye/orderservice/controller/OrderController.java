package com.tesfaye.orderservice.controller;

import com.tesfaye.orderservice.dto.OrderRequest;
import com.tesfaye.orderservice.dto.OrderResponse;
import com.tesfaye.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Create order
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(orderService.createOrder(request));
    }

    // Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable String id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // Get all orders for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @PathVariable String userId) {
        return ResponseEntity.ok(
            orderService.getOrdersByUserId(userId));
    }

    // Get all orders (admin)
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Update order status
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseEntity.ok(
            orderService.updateOrderStatus(id, status));
    }

    // Cancel order
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}