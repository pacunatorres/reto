package com.store.paymentprocessor.application.port.in;

import reactor.core.publisher.Mono;

public interface UpdatePaymentsUseCase {
    Mono<Void> update();
}
