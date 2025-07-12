package com.store.orderservice.infrastructure.adapter.out;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.azure.storage.queue.QueueAsyncClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.orderservice.domain.model.Order;

import reactor.core.publisher.Mono;

@Component
public class QueueOrderPublisher {

    private final QueueAsyncClient queueAsyncClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public QueueOrderPublisher(QueueAsyncClient queueAsyncClient, ObjectMapper objectMapper) {
        this.queueAsyncClient = queueAsyncClient;
        this.objectMapper = objectMapper;
    }
/*
    public Mono<Void> sendOrderToQueue(Order order) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(order))
                   .flatMap(json -> queueAsyncClient.sendMessage(json).then())
                   .onErrorResume(e -> {
                       // log o gestión de errores reactiva
                       return Mono.empty();
                   });
    }*/
    
    
    public Mono<Void> sendOrderToQueue(Order order) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(order))
                   .doOnNext(json -> System.out.println("Mensaje JSON a enviar: " + json))
                   .flatMap(json -> queueAsyncClient.sendMessage(json).then())
                   .doOnSuccess(unused -> System.out.println("Mensaje enviado con éxito"))
                   .doOnError(error -> System.err.println("Error al enviar mensaje: " + error.getMessage()))
                   .onErrorResume(e -> Mono.empty());
    }
}