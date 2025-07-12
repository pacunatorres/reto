package com.store.paymentprocessor.application.service;

import java.time.Instant;
import org.springframework.stereotype.Service;
import com.store.paymentprocessor.application.port.in.UpdatePaymentsUseCase;
import com.store.paymentprocessor.application.port.out.LoadPendingPaymentsPort;
import com.store.paymentprocessor.application.port.out.SaveAuditBlobPort;
import com.store.paymentprocessor.application.port.out.SavePaymentPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdatePaymentsService implements UpdatePaymentsUseCase {

    private final LoadPendingPaymentsPort loadPort;
    private final SavePaymentPort savePort;
    private final SaveAuditBlobPort auditPort;
    private final AzureQueueService azureQueueService;

    @Override
    public Mono<Void> update() {
        return loadPort.loadPendingPayments()
            .doOnNext(payment -> log.info("Procesando pago:: {}", payment.getId()))
            .map(payment -> {
                payment.setStatus("PROCESSED");
                payment.setProcessedAt(Instant.now());
                return payment;
            })
            .flatMap(payment ->
                savePort.save(payment)
                    .then(auditPort.saveAudit(payment))
                    .then(azureQueueService.sendMessage("Pago procesado: " + payment.getId()))
            )
            .then();
    }
}
