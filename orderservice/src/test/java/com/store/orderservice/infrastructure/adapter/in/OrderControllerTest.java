package com.store.orderservice.infrastructure.adapter.in;

import com.store.orderservice.application.OrderService;
import com.store.orderservice.domain.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private OrderService orderService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId("12345");
        testOrder.setProductId("Laptop");
        testOrder.setQuantity(1);
        testOrder.setPrice(1200.0);
    }

    @Test
    void createOrder_returnsOk() {
        Mockito.when(orderService.processOrder(Mockito.any(Order.class)))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testOrder)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void createOrder_returnsError_whenServiceFails() {
        Mockito.when(orderService.processOrder(Mockito.any(Order.class)))
                .thenReturn(Mono.error(new RuntimeException("Falló el envío")));

        webTestClient.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testOrder)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
