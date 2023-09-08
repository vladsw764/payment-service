package com.isariev.paymentservice;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
class PaymentServiceApplicationTests {

    private static final PostgreSQLContainer<?> postgresContainer = TestPaymentServiceApplication.postgresContainer();

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

    @Test
    void contextLoads() {

    }

}
