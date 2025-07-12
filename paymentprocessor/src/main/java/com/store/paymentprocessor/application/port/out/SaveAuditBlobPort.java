package com.store.paymentprocessor.application.port.out;

import com.store.paymentprocessor.domain.model.Payment;

import reactor.core.publisher.Mono;

public interface SaveAuditBlobPort {
    Mono<Void> saveAudit(Payment payment);
}
