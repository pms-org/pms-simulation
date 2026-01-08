package com.dtcc.simulation.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.dtcc.simulation.proto.TradeEventProto;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.Producer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RabbitStreamProducer {

    @Value("${app.rabbitmq.stream.host}")
    private String HOST;

    @Value("${app.rabbitmq.stream.port}")
    private int PORT;

    @Value("${app.rabbitmq.stream.name}")
    private String STREAM_NAME;

    @Value("${APP_RABBIT_STREAM_USERNAME:guest}")
    private String USERNAME;

    @Value("${APP_RABBIT_STREAM_PASSWORD:guest}")
    private String PASSWORD;

    @Value("${app.rabbitmq.stream.retry.max-attempts}")
    private int MAX_RETRIES;

    @Value("${app.rabbitmq.stream.retry.delay-ms}")
    private int RETRY_DELAY_MS;

    private Environment env;
    private Producer producer;

    @PostConstruct
    public void init() {
        log.info("Initializing RabbitMQ Stream Producer for host: {}, port: {}, stream: {}", HOST, PORT, STREAM_NAME);
        log.info("Using credentials - username: {}, password: {}", USERNAME, PASSWORD);
        
        env = createEnvironmentWithRetry(HOST);

        createStreamIfNotExists(env, STREAM_NAME);

        producer = env.producerBuilder()
                .stream(STREAM_NAME)
                .build();
    }

    private Environment createEnvironmentWithRetry(String host) {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < MAX_RETRIES) {
            try {
                return Environment.builder()
                        .host(host)
                        .port(PORT)
                        .username(USERNAME)
                        .password(PASSWORD)
                        .build();
            } catch (Exception e) {
                lastException = e;
                attempts++;
                log.warn("Failed to connect to RabbitMQ (Attempt {}/{}). Retrying in {}ms...", 
                         attempts, MAX_RETRIES, RETRY_DELAY_MS);
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Thread interrupted during retry", ex);
                }
            }
        }
        throw new IllegalStateException("Could not connect to RabbitMQ Stream after " + MAX_RETRIES + " attempts", lastException);
    }

    private void createStreamIfNotExists(Environment env, String streamName) {
        try {
            env.streamCreator().stream(streamName).create();
            log.info("Stream '{}' created successfully.", streamName);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("STREAM_ALREADY_EXISTS")) {
                log.info("Stream '{}' already exists, skipping creation.", streamName);
            } else {
                throw new IllegalStateException("Failed to create RabbitMQ Stream: " + streamName, e);
            }
        }
    }

    public void publish(TradeEventProto event) {
        if (producer == null) {
            throw new IllegalStateException("Producer not initialized");
        }

        if (event == null) {
            throw new IllegalArgumentException("TradeEvent cannot be null");
        }

        Message msg = producer.messageBuilder()
                .addData(event.toByteArray())
                .build();

        producer.send(msg, confirmation -> {
            if (!confirmation.isConfirmed()) {
                log.error("RabbitMQ Stream failed to confirm message for stream: {}", STREAM_NAME);
            }
        });
    }
}