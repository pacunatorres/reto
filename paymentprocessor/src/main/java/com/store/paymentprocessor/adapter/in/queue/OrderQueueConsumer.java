package com.store.paymentprocessor.adapter.in.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.paymentprocessor.adapter.out.mongobd.PaymentMongoAdapter;
import com.store.paymentprocessor.adapter.out.storage.AzureBlobAuditAdapter;
import com.store.paymentprocessor.domain.model.Order;
import com.store.paymentprocessor.domain.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;
import com.azure.storage.queue.QueueAsyncClient;
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
                String content = message.getMessageText();
                log.info("📥 Mensaje recibido: {}", content);
                try {
                    Order order = objectMapper.readValue(content, Order.class);
                    Payment payment = new Payment(
                            UUID.randomUUID().toString(),
                            order.getId(),
                            order.getPrice() * order.getQuantity(),
                            "PENDING",
                            Instant.now()
                    );
                    return paymentMongoAdapter.save(payment)
                            .then(azureBlobStorageAdapter.saveAudit(payment, "logs/payment-" + payment.getId() + ".json"))
                            .then(queueAsyncClient.deleteMessage(message.getMessageId(), message.getPopReceipt()))
                            .doOnSuccess(v -> log.info("✅ Pago procesado y mensaje eliminado"))
                            .doOnError(e -> log.error("❌ Error en la cadena de procesamiento", e));
                } catch (Exception e) {
                    log.error("❌ Error parseando mensaje", e);
                    return Mono.empty();
                }
            })
            .subscribe();
    }
}
