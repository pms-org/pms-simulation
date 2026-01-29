package com.dtcc.simulation.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dtcc.simulation.dto.TradeEvent;
import com.dtcc.simulation.repository.PortfolioIdRepository;
import com.dtcc.simulation.repository.SymbolRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeGeneratorService {

    private final PortfolioIdRepository portfolioRepo;
    private final SymbolRepository symbolRepo;

    private final Random random = new Random();

    private LocalDateTime lastTimestamp = LocalDateTime.now().minusDays(1);
    private final Map<String, String> lastSideMap = new HashMap<>();

    public TradeEvent generateTrade() {

        // Fetch lightweight lists (ONLY IDs and Symbols)
        List<UUID> portfolioIds = portfolioRepo.findAllPortfolioIds();
        List<String> symbols = symbolRepo.findAllSymbols();

        if (portfolioIds.isEmpty() || symbols.isEmpty()) {
            throw new IllegalStateException("Portfolio or Symbol table is empty");
        }

        TradeEvent t = new TradeEvent();

        UUID portfolioId = portfolioIds.get(random.nextInt(portfolioIds.size()));
        t.setPortfolioId(portfolioId);

        boolean missingFields = random.nextDouble() < 0.20;
        boolean invalidTrade = random.nextDouble() < 0.10;

        // MISSING TRADE
        if (missingFields) {
            return t;
        }

        // Pick symbol
        String symbol = symbols.get(random.nextInt(symbols.size()));
        t.setSymbol(symbol);

        // BUY / SELL alternation
        String key = portfolioId + "_" + symbol;
        String lastSide = lastSideMap.getOrDefault(key, "SELL");

        String newSide = lastSide.equals("BUY") ? "SELL" : "BUY";
        t.setSide(newSide);
        lastSideMap.put(key, newSide);

        // INVALID TRADE
        if (invalidTrade) {

            t.setTradeId(UUID.randomUUID());
            t.setPricePerStock(-1 * (10 + random.nextDouble(50)));
            t.setQuantity(-1L * (1 + random.nextInt(20)));

            updateTimestamp(t);
            return t;
        }

        // VALID TRADE
        t.setTradeId(UUID.randomUUID());
        t.setPricePerStock(100 + random.nextDouble(101));
        t.setQuantity(1 + random.nextLong(100));

        updateTimestamp(t);

        return t;
    }

    private void updateTimestamp(TradeEvent t) {
        long randomGapSeconds = 1 + random.nextInt(300);
        lastTimestamp = lastTimestamp.plusSeconds(randomGapSeconds);
        
        // Ensure timestamp never goes into the future
        LocalDateTime now = LocalDateTime.now();
        if (lastTimestamp.isAfter(now)) {
            lastTimestamp = now.minusHours(1); // Reset to 1 hour ago if we've caught up to present
        }
        
        t.setTimestamp(lastTimestamp);
    }
}