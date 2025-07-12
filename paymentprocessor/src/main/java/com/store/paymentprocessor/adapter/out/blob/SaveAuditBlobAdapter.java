package com.store.paymentprocessor.adapter.out.blob;

import org.springframework.stereotype.Component;

import com.store.paymentprocessor.adapter.out.storage.AzureBlobAuditAdapter;
import com.store.paymentprocessor.application.port.out.SaveAuditBlobPort;
import com.store.paymentprocessor.domain.model.Payment;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class SaveAuditBlobAdapter implements SaveAuditBlobPort {
    private final AzureBlobAuditAdapter azureBlobAuditAdapter;
	@Override
	public Mono<Void> saveAudit(Payment payment) {
		// TODO Auto-generated method stub
        String fileName = "audit-" + payment.getId() + ".json";

		return azureBlobAuditAdapter.saveAudit(payment, fileName);
	}
}
