package com.store.paymentprocessor.domain.exception;

public class AuditUploadException extends RuntimeException {
    public AuditUploadException(String message, Throwable cause) {
        super(message, cause);
    }
    public AuditUploadException(String message) {
        super(message);
    }
}