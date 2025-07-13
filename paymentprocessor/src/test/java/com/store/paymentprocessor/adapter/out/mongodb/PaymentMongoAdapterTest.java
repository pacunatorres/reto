package com.store.paymentprocessor.adapter.out.mongodb;
import com.store.paymentprocessor.adapter.out.mongobd.MongoPaymentRepository;
import com.store.paymentprocessor.adapter.out.mongobd.PaymentDocument;
import com.store.paymentprocessor.adapter.out.mongobd.PaymentMongoAdapter;
import com.store.paymentprocessor.domain.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.Instant;
import static org.mockito.Mockito.*;

class PaymentMongoAdapterTest {

    private MongoPaymentRepository repository;
    private PaymentMongoAdapter adapter;
    private PaymentDocument mockDoc;
    private Payment mockPayment;

    @BeforeEach
    void setUp() {
        repository = mock(MongoPaymentRepository.class);
        adapter = new PaymentMongoAdapter(repository);

        mockDoc = new PaymentDocument("1", "order-123", 100.0, "PENDING", Instant.now());
        mockPayment = new Payment("1", "order-123", 100.0, "PENDING", mockDoc.getTimestamp());
    }

    @Test
    void loadPendingPayments_shouldReturnPendingPayments() {
        when(repository.findByStatus("PENDING"))
            .thenReturn(Flux.just(mockDoc));

        StepVerifier.create(adapter.loadPendingPayments())
            .expectNextMatches(payment ->
                payment.getId().equals(mockDoc.getId()) &&
                payment.getOrderId().equals(mockDoc.getOrderId()) &&
                payment.getStatus().equals("PENDING")
            )
            .verifyComplete();
        verify(repository, times(1)).findByStatus("PENDING");
    }

    @Test
    void save_shouldSavePaymentSuccessfully() {
        when(repository.save(any(PaymentDocument.class)))
            .thenReturn(Mono.just(mockDoc));

        StepVerifier.create(adapter.save(mockPayment))
            .verifyComplete();

        verify(repository, times(1)).save(any(PaymentDocument.class));
    }
}
