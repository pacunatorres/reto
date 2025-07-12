package com.store.paymentprocessor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.blob.BlobClientBuilder;

@Configuration
public class AzureStorageConfig {

    @Bean
    public BlobClientBuilder blobClientBuilder() {
        return new BlobClientBuilder()
                .connectionString("DefaultEndpointsProtocol=https;AccountName=blobstorageaccountpacuna;AccountKey=g5lrfMwaQVa/eC/m89LQ3GXVh22ktZxRpoTAM6e3gHuxX7vzztRpFpBB6m9mf+PFeBn5G7V6Cgcf+AStjlQKFw==;EndpointSuffix=core.windows.net");
    }
}