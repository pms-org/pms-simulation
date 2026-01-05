package com.dtcc.simulation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class TradeEvent {

    private UUID portfolioId;
    private UUID tradeId;
    private String symbol;
    private String side;
    private Double pricePerStock;
    private Long quantity;
    private LocalDateTime timestamp;
}
