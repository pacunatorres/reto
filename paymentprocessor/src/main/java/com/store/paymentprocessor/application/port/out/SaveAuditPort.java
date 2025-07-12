package com.store.paymentprocessor.application.port.out;

import reactor.core.publisher.Mono;

public interface SaveAuditPort {
    Mono<Void> saveAudit(Object event, String fileName);
}
