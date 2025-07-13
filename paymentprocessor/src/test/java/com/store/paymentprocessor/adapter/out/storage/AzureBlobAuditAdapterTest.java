package com.store.paymentprocessor.adapter.out.storage;


import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.paymentprocessor.domain.exception.AuditUploadException;
import com.store.paymentprocessor.domain.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.*;

class AzureBlobAuditAdapterTest {

    private BlobServiceAsyncClient blobServiceAsyncClient;
    private BlobContainerAsyncClient blobContainerAsyncClient;
    private BlobAsyncClient blobAsyncClient;
    private ObjectMapper objectMapper;
    private AzureBlobAuditAdapter adapter;

    @BeforeEach
    void setUp() {
        blobServiceAsyncClient = mock(BlobServiceAsyncClient.class);
        blobContainerAsyncClient = mock(BlobContainerAsyncClient.class);
        blobAsyncClient = mock(BlobAsyncClient.class);
        objectMapper = new ObjectMapper();

        adapter = new AzureBlobAuditAdapter(blobServiceAsyncClient, objectMapper);

        try {
            var containerField = AzureBlobAuditAdapter.class.getDeclaredField("containerName");
            containerField.setAccessible(true);
            containerField.set(adapter, "test-container");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error configurando containerName para el test", e);
        }
    }

    @Test
    void saveAudit_shouldUploadJsonToBlob() throws Exception {
        Payment payment = new Payment("1", "order-1", 100.0, "PENDING", null);
        String expectedFileName = "logs/payment-1.json";

        when(blobServiceAsyncClient.getBlobContainerAsyncClient("test-container"))
            .thenReturn(blobContainerAsyncClient);
        when(blobContainerAsyncClient.getBlobAsyncClient(expectedFileName))
            .thenReturn(blobAsyncClient);
        when(blobAsyncClient.upload(any(BinaryData.class), eq(true)))
            .thenReturn(Mono.empty());

        StepVerifier.create(adapter.saveAudit(payment, expectedFileName))
            .verifyComplete();

        verify(blobAsyncClient, times(1)).upload(any(BinaryData.class), eq(true));
    }

    @Test
    void saveAudit_shouldHandleSerializationError() throws Exception {
        ObjectMapper faultyMapper = mock(ObjectMapper.class);
        adapter = new AzureBlobAuditAdapter(blobServiceAsyncClient, faultyMapper);
        var containerField = AzureBlobAuditAdapter.class.getDeclaredField("containerName");
        containerField.setAccessible(true);
        containerField.set(adapter, "test-container");

        Object faultyObject = new Object();
        when(faultyMapper.writeValueAsString(faultyObject))
            .thenThrow(new JsonProcessingException("Mock error") {});
        StepVerifier.create(adapter.saveAudit(faultyObject, "any.json"))
            .expectErrorMatches(err -> err instanceof AuditUploadException &&
                                       err.getMessage().contains("serializando"))
            .verify();

        verify(blobServiceAsyncClient, never()).getBlobContainerAsyncClient(any());
    }
}
