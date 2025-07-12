package com.store.paymentprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.store.paymentprocessor.config.AzureBlobConfig;

@SpringBootApplication
@EnableScheduling //
@EnableConfigurationProperties(AzureBlobConfig.class)

public class PaymentprocessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentprocessorApplication.class, args);
	}

}
