package com.tesfaye.orderservice.dto;

import com.tesfaye.orderservice.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String id;
    private String userId;
    private String userEmail;
    private List<OrderItem> items;
    private double totalAmount;
    private String status;
    private LocalDateTime createdAt;
}