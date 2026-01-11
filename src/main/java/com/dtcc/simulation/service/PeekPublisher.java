// package com.dtcc.simulation.service;


// import org.springframework.amqp.rabbit.core.RabbitTemplate;
// import org.springframework.stereotype.Service;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class PeekPublisher {

//     private final RabbitTemplate rabbitTemplate;

//     public void send(String exchange, String routingKey, Object message) {

//         log.info("PEEK: Sending RabbitMQ Message → {}", message);

//         rabbitTemplate.convertAndSend(exchange, routingKey, message);
//     }
// }
