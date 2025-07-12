package com.store.paymentprocessor.adapter.out.blob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Component;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.paymentprocessor.application.port.out.SaveAuditBlobPort;
import com.store.paymentprocessor.domain.model.Payment;


@Slf4j
@Component
@RequiredArgsConstructor
public class AzureBlobAdapter implements SaveAuditBlobPort {

    private final BlobServiceAsyncClient blobServiceAsyncClient;
    private final ObjectMapper objectMapper;

    private static final String CONTAINER_NAME = "conteiner-logs"; // Cambia por tu contenedor real

    @Override
    public Mono<Void> saveAudit(Payment payment) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(payment))
            .flatMap(json -> {
                String fileName = "audit-" + payment.getId() + ".json";
                BlobAsyncClient blobClient = blobServiceAsyncClient
                    .getBlobContainerAsyncClient(CONTAINER_NAME)
                    .getBlobAsyncClient(fileName);

                return blobClient.upload(BinaryData.fromString(json), true)
                    .doOnSuccess(r -> log.info("Audit almacenado en Azure Blob: {}", fileName))
                    .doOnError(e -> log.error("Error al guardar auditoría en blob", e));
            })
            .then();
    }
}
