package com.store.paymentprocessor.adapter.out.mongobd;

import java.util.List;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.store.paymentprocessor.domain.model.Payment;

import reactor.core.publisher.Flux;

public interface MongoPaymentRepository extends ReactiveMongoRepository<PaymentDocument, String> {
    Flux<PaymentDocument> findByStatus(String status);
}