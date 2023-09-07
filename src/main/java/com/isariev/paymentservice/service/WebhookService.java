package com.isariev.paymentservice.service;

import com.isariev.paymentservice.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Service for sending webhook requests to another endpoint.(In reality to external system.)
 */
@Service
@Slf4j
public class WebhookService {

    private final WebClient webClient;

    public WebhookService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Sends a webhook request to the specified URL with the given transaction data.
     *
     * @param transaction The transaction data to include in the webhook request body.
     * @return A Mono representing the completion of the webhook request.
     */
    public Mono<Void> sendTransactionWebhook(Transaction transaction) {
        return webClient.post()
                .uri(transaction.getNotificationUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(transaction))
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        log.info("Webhook request to {} was successful. HTTP Status: {}", transaction.getNotificationUrl(), responseEntity.getStatusCode());
                    } else {
                        log.error("Failed to send webhook to {}. HTTP Status: {}", transaction.getNotificationUrl(), responseEntity.getStatusCode());
                    }
                })
                .then();
    }
}
