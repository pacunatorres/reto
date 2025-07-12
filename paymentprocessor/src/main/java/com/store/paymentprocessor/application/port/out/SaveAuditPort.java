package com.store.paymentprocessor.application.port.out;


public interface SaveAuditPort {
    void saveAudit(Object event, String fileName);
}
