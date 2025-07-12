package com.store.paymentprocessor.adapter.out.storage;


import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.core.util.BinaryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.paymentprocessor.domain.exception.AuditUploadException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.azure.storage.blob.BlobAsyncClient;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AzureBlobAuditAdapter {

    private final BlobServiceAsyncClient blobServiceAsyncClient;
    private final ObjectMapper objectMapper;

    @Value("${azure.blob.container-name}")
    private String containerName;
    
    public Mono<Void> saveAudit(Object event, String fileName) {
        return serializeEvent(event)
            .flatMap(json -> {
                BlobAsyncClient blobClient = blobServiceAsyncClient
                    .getBlobContainerAsyncClient(containerName)
                    .getBlobAsyncClient(fileName);
                return blobClient.upload(BinaryData.fromString(json), true)
                    .doOnSuccess(response -> log.info("Audit almacenado en Azure Blob: {}", fileName))
                    .doOnError(err -> log.error("Error durante la subida del blob: {}", fileName, err))
                    .onErrorMap(err -> new AuditUploadException("Error al subir auditoría a Azure Blob", err));
            })
            .then();
    }

    private Mono<String> serializeEvent(Object event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            return Mono.just(json);
        } catch (JsonProcessingException e) {
            log.error("Error serializando el evento para auditoría", e);
            return Mono.error(new AuditUploadException("Error serializando auditoría", e));
        }
    }
}
