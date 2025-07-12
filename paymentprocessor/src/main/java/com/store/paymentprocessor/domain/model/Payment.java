package com.store.paymentprocessor.domain.model;


import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private String id;
    private String orderId;
    private Double amount;
    private String status;
    private Instant processedAt;
}