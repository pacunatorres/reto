package com.store.paymentprocessor.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.azure.storage.queue.QueueAsyncClient;

import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureQueueService {

    private final QueueAsyncClient queueAsyncClient;

    public Mono<Void> sendMessage(String message) {
        return Mono.from(queueAsyncClient.sendMessage(message))
            .doOnSuccess(response -> log.info("Mensaje enviado a Azure Queue: {}", response.getMessageId()))
            .doOnError(error -> log.error("Error al enviar mensaje a Azure Queue", error))
            .then(); // Devolver Mono<Void>
    }
}