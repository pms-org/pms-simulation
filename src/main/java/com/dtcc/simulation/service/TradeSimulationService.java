package com.dtcc.simulation.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dtcc.simulation.proto.TradeEventProto;
import com.google.protobuf.Timestamp;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeSimulationService {

    @Autowired
    private TradeGeneratorService generator;
    @Autowired
    private RabbitStreamProducer producer;
    private Random random = new Random();

    private enum SpeedMode {

        SLOW(800),       // 800 ms
        FAST(400),       // 400 ms
        VERY_FAST(200),  // 200 ms
        PAUSE(5000);     // 5 seconds

        long delayMs;
        SpeedMode(long d) { this.delayMs = d; }
  }


    @PostConstruct
    public void start() {

        Thread simulationThread = new Thread(() -> {

            SpeedMode[] modes = SpeedMode.values();

            while (!Thread.currentThread().isInterrupted()) {

                var event = generator.generateTrade();

                TradeEventProto.Builder builder = TradeEventProto.newBuilder()
                        .setPortfolioId(event.getPortfolioId().toString());

                if (event.getTradeId() != null)
                    builder.setTradeId(event.getTradeId().toString());

                if (event.getSymbol() != null)
                    builder.setSymbol(event.getSymbol());

                if (event.getSide() != null)
                    builder.setSide(event.getSide());

                if (event.getPricePerStock() != null)
                    builder.setPricePerStock(event.getPricePerStock());

                if (event.getQuantity() != null)
                    builder.setQuantity(event.getQuantity());

                if (event.getTimestamp() != null)
                    builder.setTimestamp(Timestamp.newBuilder()
                        .setSeconds(event.getTimestamp().toEpochSecond(java.time.ZoneOffset.UTC))
                        .build());

                TradeEventProto proto = builder.build();

                producer.publish(proto);

                SpeedMode mode = modes[random.nextInt(modes.length)];

                try {
                    Thread.sleep(mode.delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        simulationThread.setDaemon(true);
        simulationThread.start();
    }
}

        
