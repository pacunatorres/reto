package com.store.paymentprocessor.adapter.in.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.store.paymentprocessor.application.port.in.UpdatePaymentsUseCase;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentBatchJob {

    private final UpdatePaymentsUseCase updatePaymentsUseCase;

    @Scheduled(fixedRate = 15_000) // cada 10 minutos
    public void run() {
        log.info("Lanzando batch de actualización de pagos...");
        updatePaymentsUseCase.update();
    }
}