package com.isariev.paymentservice.controller;

import com.isariev.paymentservice.TestPaymentServiceApplication;
import com.isariev.paymentservice.dto.request.CustomerRequestDto;
import com.isariev.paymentservice.dto.request.TransactionRequestDto;
import com.isariev.paymentservice.dto.response.TransactionResponseDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class TransactionStatusControllerIntegrationTest {

    private static final PostgreSQLContainer<?> postgresContainer = TestPaymentServiceApplication.postgresContainer();

    @Autowired
    private WebTestClient testClient;

    private TransactionRequestDto requestDto;

    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://localhost:7777/integration-tests-db");
        registry.add("spring.r2dbc.username", () -> "postgres");
        registry.add("spring.r2dbc.password", () -> "postgres");
        registry.add("spring.flyway.url", () -> "jdbc:postgresql://localhost:7777/integration-tests-db");
        registry.add("spring.flyway.username", () -> "postgres");
        registry.add("spring.flyway.password", () -> "postgres");
    }

    @BeforeAll
    static void startContainer() {
        if (!postgresContainer.isRunning()) {
            postgresContainer.start();
        }
    }

    @AfterAll
    static void stopContainer() {
        postgresContainer.stop();
    }

    @BeforeEach
    void setUp() {
        requestDto = new TransactionRequestDto(
                7800,
                "USD",
                "http://localhost:" + port + "/v1/status",
                "merchant.com/main",
                new CustomerRequestDto(
                        "Vlad",
                        "Isariev",
                        "vladsw76@gmail.com",
                        "UA",
                        "88005553535"
                ),
                true
        );
    }

    @Test
    void getTransactionStatusById() {
        testClient.get()
                .uri("/v1/transactions/{uid}", createTransactionAndGetId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> assertThat(responseBody).isNotNull());
    }


    private String createTransactionAndGetId() {
        TransactionResponseDto responseDto = testClient.post()
                .uri("/v1/transactions")
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.uid()).isNotNull();

        return responseDto.uid();
    }
}
