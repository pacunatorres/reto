package com.store.paymentprocessor.application.port.out;

import com.store.paymentprocessor.domain.model.Payment;

import reactor.core.publisher.Mono;

public interface SavePaymentPort {
    Mono<Void> save(Payment payment);
}
