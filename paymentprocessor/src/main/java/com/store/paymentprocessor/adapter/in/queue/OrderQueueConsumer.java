package com.store.paymentprocessor.adapter.in.queue;

import com.azure.storage.queue.QueueClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.paymentprocessor.adapter.out.mongobd.PaymentMongoAdapter;
import com.store.paymentprocessor.adapter.out.storage.AzureBlobAuditAdapter;
import com.store.paymentprocessor.domain.model.Order;
import com.store.paymentprocessor.domain.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;
import com.azure.storage.queue.QueueAsyncClient;
import com.azure.storage.queue.models.QueueMessageItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class OrderQueueConsumer {

    private final QueueAsyncClient queueAsyncClient;
    private final PaymentMongoAdapter paymentMongoAdapter;
    private final AzureBlobAuditAdapter azureBlobStorageAdapter;
    private final ObjectMapper objectMapper;

    public OrderQueueConsumer(
            QueueAsyncClient queueAsyncClient,
            PaymentMongoAdapter paymentMongoAdapter,
            AzureBlobAuditAdapter azureBlobStorageAdapter
    ) {
        this.queueAsyncClient = queueAsyncClient;
        this.paymentMongoAdapter = paymentMongoAdapter;
        this.azureBlobStorageAdapter = azureBlobStorageAdapter;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }
    @Scheduled(fixedRate = 5000)
    public void consumeMessages() {
        queueAsyncClient.receiveMessages(5)
            .flatMap(message -> {
                return Mono.fromCallable(() -> {
                    String content = message.getMessageText();
                    log.info("📥 Mensaje recibido:: {}", content);

                    Order order = objectMapper.readValue(content, Order.class);
                    log.info("📥 order: : {}", order);

                    Payment payment = new Payment(
                            UUID.randomUUID().toString(),
                            order.getId(),
                            order.getPrice() * order.getQuantity(),
                            "PENDING",
                            Instant.now()
                    );
                    log.info("📥 payment: : {}", payment);
                    paymentMongoAdapter.save(payment); // suponiendo que esto es void
                    log.info("✅ payment {}",payment);

                    azureBlobStorageAdapter.saveAudit(payment, "logs/payment-" + payment.getId() + ".json"); // también void

                    queueAsyncClient.deleteMessage(message.getMessageId(), message.getPopReceipt())
                        .subscribe(); // este sí es reactivo

                    log.info("✅ Pago procesado y mensaje eliminado");
                    return true;
                }).onErrorResume(e -> {
                    log.error("❌ Error procesando mensaje", e);
                    return Mono.empty();
                });
            })
            .subscribe();
    }
}
