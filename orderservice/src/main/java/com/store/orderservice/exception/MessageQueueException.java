package com.store.orderservice.exception;

public class MessageQueueException extends RuntimeException {
    public MessageQueueException(String message, Throwable cause) {
        super(message, cause);
    }
}