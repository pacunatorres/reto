package com.store.paymentprocessor.application.port.out;

import com.store.paymentprocessor.domain.model.Payment;
import reactor.core.publisher.Flux;

public interface LoadPendingPaymentsPort {
    Flux<Payment> loadPendingPayments();
}