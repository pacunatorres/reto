package com.store.paymentprocessor.adapter.out.storage;


import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.core.util.BinaryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.azure.storage.blob.BlobAsyncClient;


@Component
@RequiredArgsConstructor
@Slf4j
public class AzureBlobAuditAdapter {

    private final BlobServiceAsyncClient blobServiceAsyncClient;
    private final ObjectMapper objectMapper;

    private static final String CONTAINER_NAME = "containerlogs";

    /**
     * Guarda cualquier objeto como archivo JSON en Azure Blob Storage.
     *
     * @param event    Objeto a serializar
     * @param fileName Nombre del archivo (por ejemplo: audit-123.json)
     * @return Mono<Void>
     */
    public Mono<Void> saveAudit(Object event, String fileName) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
            .flatMap(json -> {
                BlobAsyncClient blobClient = blobServiceAsyncClient
                    .getBlobContainerAsyncClient(CONTAINER_NAME)
                    .getBlobAsyncClient(fileName);

                return blobClient.upload(BinaryData.fromString(json), true);
            })
            .doOnSuccess(r -> log.info("Audit almacenado en Azure Blob: {}", fileName))
            .doOnError(e -> log.error("Error al guardar auditoría en blob", e))
            .then();
    }
}
