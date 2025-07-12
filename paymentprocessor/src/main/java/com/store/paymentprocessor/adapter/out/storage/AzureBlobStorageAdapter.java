package com.store.paymentprocessor.adapter.out.storage;

import com.azure.storage.blob.BlobClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.paymentprocessor.application.port.out.SaveAuditPort;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;

@Component
public class AzureBlobStorageAdapter implements SaveAuditPort {

    private final ObjectMapper objectMapper;
    private final BlobClientBuilder blobClientBuilder;

    public AzureBlobStorageAdapter(BlobClientBuilder blobClientBuilder, ObjectMapper objectMapper) {
        this.blobClientBuilder = blobClientBuilder;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveAudit(Object event, String fileName) {
        try {
            byte[] data = objectMapper.writeValueAsBytes(event);
            blobClientBuilder.blobName(fileName)
                             .buildClient()
                             .upload(new ByteArrayInputStream(data), data.length, true);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading audit to Blob", e);
        }
    }
}