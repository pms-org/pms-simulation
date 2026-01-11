package com.dtcc.simulation.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dtcc.simulation.proto.TradeEventProto;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.Producer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RabbitStreamProducer {

    @Value("${app.rabbitmq.stream.host}")
    private String host;

    @Value("${app.rabbitmq.stream.port}")
    private int port;

    @Value("${app.rabbitmq.stream.name}")
    private String streamName;

    @Value("${app.rabbitmq.stream.username}")
    private String username;

    @Value("${app.rabbitmq.stream.password}")
    private String password;

    private static final int MAX_RETRIES = 10;
    private static final int RETRY_DELAY_MS = 3000;

    private Environment env;
    private Producer producer;

    @PostConstruct
    public void init() {
        env = createEnvironmentWithRetry();
        createStreamIfNotExists(env, streamName);

        producer = env.producerBuilder()
                .stream(streamName)
                .build();
    }

    private Environment createEnvironmentWithRetry() {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < MAX_RETRIES) {
            try {
                return Environment.builder()
                        .host(host)
                        .port(port)
                        .username(username)
                        .password(password)
                        .build();
            } catch (Exception e) {
                lastException = e;
                attempts++;
                log.warn("RabbitMQ Stream connection failed. Retrying... ({}/{})", attempts, MAX_RETRIES);
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Retry interrupted", ex);
                }
            }
        }
        throw new IllegalStateException("Failed to connect to RabbitMQ Stream", lastException);
    }

    private void createStreamIfNotExists(Environment env, String streamName) {
        try {
            env.streamCreator().stream(streamName).create();
        } catch (Exception e) {
            if (!e.getMessage().contains("STREAM_ALREADY_EXISTS")) {
                throw new IllegalStateException("Failed to create RabbitMQ Stream: " + streamName, e);
            }
        }
    }

    public void publish(TradeEventProto event) {
        if (event == null) {
            throw new IllegalArgumentException("TradeEvent cannot be null");
        }

        Message msg = producer.messageBuilder()
                .addData(event.toByteArray())
                .build();

        producer.send(msg, confirmation -> {
            if (!confirmation.isConfirmed()) {
                throw new RuntimeException("RabbitMQ Stream failed to confirm message");
            }
        });
    }
}
