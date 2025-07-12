package com.store.paymentprocessor.adapter.in.queue;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.paymentprocessor.adapter.out.mongobd.PaymentMongoAdapter;
import com.store.paymentprocessor.adapter.out.storage.AzureBlobStorageAdapter;
import com.store.paymentprocessor.domain.model.Order;
import com.store.paymentprocessor.domain.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class OrderQueueConsumer {
    private final QueueClient queueClient;
    private final PaymentMongoAdapter paymentMongoAdapter;
    private final AzureBlobStorageAdapter azureBlobStorageAdapter;
    private final ObjectMapper objectMapper;

    public OrderQueueConsumer(
            @Value("${azure.storage.connection-string}") String connectionString,
            @Value("${azure.storage.queue-name}") String queueName,
            PaymentMongoAdapter paymentMongoAdapter,
            AzureBlobStorageAdapter azureBlobStorageAdapter,
            ObjectMapper objectMapper 
            ) {

        this.queueClient = new QueueClientBuilder()
                .connectionString(connectionString)
                .queueName(queueName)
                .buildClient();

        this.queueClient.createIfNotExists();
        this.paymentMongoAdapter = paymentMongoAdapter;
        this.azureBlobStorageAdapter = azureBlobStorageAdapter;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 5000) // cada 5 segundos
    public void consumeMessages() {
        queueClient.receiveMessages(5).forEach(message -> {
            String content = message.getMessageText();
            log.info("📥 Mensaje recibido de la cola: {}", content);

            try {
                Order order = objectMapper.readValue(content, Order.class);
                Payment payment = new Payment(
                        UUID.randomUUID().toString(),
                        order.getId(),
                        order.getPrice() * order.getQuantity(),
                        "PENDING",
                        Instant.now()
                );
                paymentMongoAdapter.save(payment);
                log.info("✅ Payment guardado en Mongo: {}", payment);
                azureBlobStorageAdapter.saveAudit(payment, "logs/payment-" + payment.getId() + ".json");
                log.info("☁️ Payment audit guardado en Blob");
            } catch (Exception e) {
                log.error("❌ Error procesando mensaje: {}", content, e);
            }
            queueClient.deleteMessage(message.getMessageId(), message.getPopReceipt());
        });
    }
}