package com.store.paymentprocessor.adapter.out.mongobd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class PaymentDocument {
    @Id
    private String id;
    private String orderId;
    private Double amount;
    private String status;
    private Instant timestamp;
}