package com.store.orderservice.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.store.orderservice.domain.model.Order;
import com.store.orderservice.infrastructure.adapter.out.QueueOrderPublisher;

import reactor.core.publisher.Mono;

@Service
public class OrderService {

    private final QueueOrderPublisher queuePublisher;

    @Autowired
    public OrderService(QueueOrderPublisher queuePublisher) {
        this.queuePublisher = queuePublisher;
    }

    public Mono<Void> processOrder(Order order) {
        // Aquí podrías validar, aplicar lógica de negocio, etc.
    	System.out.println(" processOrder.."+order);
        return queuePublisher.sendOrderToQueue(order);
    }
}