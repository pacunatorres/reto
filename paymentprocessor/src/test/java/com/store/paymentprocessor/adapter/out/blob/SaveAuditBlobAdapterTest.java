package com.store.paymentprocessor.adapter.out.blob;

import com.store.paymentprocessor.adapter.out.storage.AzureBlobAuditAdapter;
import com.store.paymentprocessor.domain.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SaveAuditBlobAdapterTest {

    private AzureBlobAuditAdapter azureBlobAuditAdapter;
    private SaveAuditBlobAdapter saveAuditBlobAdapter;

    @BeforeEach
    void setUp() {
        azureBlobAuditAdapter = mock(AzureBlobAuditAdapter.class);
        saveAuditBlobAdapter = new SaveAuditBlobAdapter(azureBlobAuditAdapter);
    }

    @Test
    void testSaveAudit_shouldCallAdapterAndReturnMonoVoid() {
        Payment payment = new Payment();
        payment.setId("12345");

        when(azureBlobAuditAdapter.saveAudit(any(Payment.class), anyString()))
                .thenReturn(Mono.empty());
        StepVerifier.create(saveAuditBlobAdapter.saveAudit(payment))
                .verifyComplete();

        verify(azureBlobAuditAdapter, times(1))
                .saveAudit(eq(payment), eq("audit-12345.json"));
    }
}
