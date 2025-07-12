package com.store.orderservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor

public class Order {
    private String id;
    private String customerId;
    private String productId;
    private int quantity;
    private double price;
    
    public Order() {
    }
}

