package com.store.orderservice.infrastructure.adapter.out;

import com.azure.storage.queue.QueueAsyncClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.orderservice.domain.model.Order;
import com.store.orderservice.exception.MessageQueueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
public class QueueOrderPublisherTest {

    private QueueAsyncClient queueAsyncClient;
    private ObjectMapper objectMapper;
    private QueueOrderPublisher queueOrderPublisher;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        queueAsyncClient = mock(QueueAsyncClient.class);
        objectMapper = new ObjectMapper();
        queueOrderPublisher = new QueueOrderPublisher(queueAsyncClient, objectMapper);

        testOrder = new Order();
        testOrder.setId("123");
        testOrder.setProductId("Laptop");
        testOrder.setQuantity(2);
        testOrder.setPrice(1500.0);
    }

    @Test
    void sendOrderToQueue_success() {
        when(queueAsyncClient.sendMessage(anyString()))
            .thenReturn(Mono.empty());

        Mono<Void> result = queueOrderPublisher.sendOrderToQueue(testOrder);

        StepVerifier.create(result)
            .verifyComplete();

        verify(queueAsyncClient, times(1)).sendMessage(anyString());
    }

    @Test
    void sendOrderToQueue_jsonError() {
        Order invalidOrder = mock(Order.class);
        try {
            when(invalidOrder.getId()).thenThrow(new RuntimeException("No se puede serializar"));
        } catch (Exception ignored) {}

        Mono<Void> result = queueOrderPublisher.sendOrderToQueue(invalidOrder);

        StepVerifier.create(result)
            .expectError(MessageQueueException.class)
            .verify();
    }

    @Test
    void sendOrderToQueue_queueFails() {
        when(queueAsyncClient.sendMessage(anyString()))
            .thenReturn(Mono.error(new RuntimeException("Error en Azure Queue")));

        Mono<Void> result = queueOrderPublisher.sendOrderToQueue(testOrder);

        StepVerifier.create(result)
            .expectError(MessageQueueException.class)
            .verify();
    }
}
