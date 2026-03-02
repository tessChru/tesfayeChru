package com.tesfaye.orderservice.service;

import com.tesfaye.orderservice.dto.OrderRequest;
import com.tesfaye.orderservice.dto.OrderResponse;
import com.tesfaye.orderservice.exception.OrderNotFoundException;
import com.tesfaye.orderservice.model.Order;
import com.tesfaye.orderservice.model.OrderItem;
import com.tesfaye.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderResponse createOrder(OrderRequest request) {
        // Map items and calculate total
        List<OrderItem> items = request.getItems().stream()
            .map(item -> OrderItem.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build())
            .collect(Collectors.toList());

        double totalAmount = items.stream()
            .mapToDouble(i -> i.getPrice() * i.getQuantity())
            .sum();

        // Build and save order
        Order order = Order.builder()
            .userId(request.getUserId())
            .userEmail(request.getUserEmail())
            .items(items)
            .totalAmount(totalAmount)
            .status("PENDING")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Order saved = orderRepository.save(order);
        log.info("Order created: {} for user: {}",
            saved.getId(), saved.getUserEmail());

        return mapToResponse(saved);
    }

    public OrderResponse getOrderById(String id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(
                "Order not found with id: " + id));
        return mapToResponse(order);
    }

    public List<OrderResponse> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public OrderResponse updateOrderStatus(String id, String status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(
                "Order not found with id: " + id));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);

        log.info("Order {} status updated to: {}", id, status);
        return mapToResponse(updated);
    }

    public void cancelOrder(String id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(
                "Order not found with id: " + id));

        order.setStatus("CANCELLED");
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        log.info("Order cancelled: {}", id);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
            .id(order.getId())
            .userId(order.getUserId())
            .userEmail(order.getUserEmail())
            .items(order.getItems())
            .totalAmount(order.getTotalAmount())
            .status(order.getStatus())
            .createdAt(order.getCreatedAt())
            .build();
    }
}  