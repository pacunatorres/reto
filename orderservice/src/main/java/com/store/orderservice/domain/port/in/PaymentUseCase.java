package com.store.orderservice.domain.port.in;

import com.store.orderservice.domain.model.Order;
import reactor.core.publisher.Mono;

public interface PaymentUseCase {
    Mono<Void> processPayment(Order order);
}