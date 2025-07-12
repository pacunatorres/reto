package com.store.paymentprocessor.adapter.out.mongobd;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Component;
import com.store.paymentprocessor.application.port.out.LoadPendingPaymentsPort;
import com.store.paymentprocessor.application.port.out.SavePaymentPort;
import com.store.paymentprocessor.domain.model.Payment;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentMongoAdapter implements LoadPendingPaymentsPort, SavePaymentPort {

    private final MongoPaymentRepository repository;

    @Override
    public Flux<Payment> loadPendingPayments() {
        return repository.findByStatus("PENDING")
                .map(this::toDomain);
    }

    @Override
    public Mono<Void> save(Payment payment) {
        return repository.save(toDocument(payment))
                .then(); 
    }

    private PaymentDocument toDocument(Payment payment) {
        return new PaymentDocument(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getProcessedAt()
        );
    }

    private Payment toDomain(PaymentDocument doc) {
        return new Payment(
                doc.getId(),
                doc.getOrderId(),
                doc.getAmount(),
                doc.getStatus(),
                doc.getTimestamp()
        );
    }
}