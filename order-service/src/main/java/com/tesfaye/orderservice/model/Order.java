package com.tesfaye.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private String userId;
    private String userEmail;
    private List<OrderItem> items;
    private double totalAmount;
    private String status; // PENDING, CONFIRMED, CANCELLED, DELIVERED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
