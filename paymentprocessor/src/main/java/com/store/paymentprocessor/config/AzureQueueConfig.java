package com.store.paymentprocessor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.azure.storage.queue.QueueAsyncClient;
import com.azure.storage.queue.QueueClientBuilder;
@Configuration
public class AzureQueueConfig {

    @Value("${azure.queue.connection-string}")
    private String connectionString;

    @Value("${azure.queue.name}")
    private String queueName;

    @Bean
    public QueueAsyncClient queueAsyncClient() {
        return new QueueClientBuilder()
                .connectionString(connectionString)
                .queueName(queueName)
                .buildAsyncClient();
    }
}