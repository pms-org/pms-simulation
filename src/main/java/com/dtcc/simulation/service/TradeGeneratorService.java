package com.dtcc.simulation.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dtcc.simulation.dto.TradeEvent;
import com.dtcc.simulation.entity.PortfolioId;
import com.dtcc.simulation.entity.Symbol;
import com.dtcc.simulation.repository.PortfolioIdRepository;
import com.dtcc.simulation.repository.SymbolRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeGeneratorService {

    private final PortfolioIdRepository portfolioRepo;
    private final SymbolRepository symbolRepo;

    private final Random random = new Random();

    private List<UUID> cachedPortfolios;
    private List<String> cachedSymbols;

    private LocalDateTime lastTimestamp = LocalDateTime.now().minusDays(1);
    private final Map<String, String> lastSideMap = new HashMap<>();

    @PostConstruct
    public void loadInitialDataFromDB() {
        this.cachedSymbols = symbolRepo.findAll()
                .stream()
                .map(Symbol::getSymbol)
                .toList();

        updatePortfolioIdCache();
    }

    @Scheduled(fixedRate = 30000)
    public void updatePortfolioIdCache() {
        List<UUID> newPortfolios = portfolioRepo.findAll()
                .stream()
                .map(PortfolioId::getPortfolio_id)
                .toList();

        this.cachedPortfolios = newPortfolios;
    }

    public TradeEvent generateTrade() {

        if (cachedPortfolios == null || cachedPortfolios.isEmpty() ||
                cachedSymbols == null || cachedSymbols.isEmpty()) {
            throw new IllegalStateException("Trade generator cache is empty. Cannot generate trade.");
        }

        TradeEvent t = new TradeEvent();

        UUID portfolioId = cachedPortfolios.get(random.nextInt(cachedPortfolios.size()));
        t.setPortfolioId(portfolioId);

        boolean missingFields = random.nextDouble() < 0.20;
        boolean invalidTrade = random.nextDouble() < 0.10;

        // MISSING TRADE
        if (missingFields) {
            return t;
        }

        // Pick a symbol
        String symbol = cachedSymbols.get(random.nextInt(cachedSymbols.size()));
        t.setSymbol(symbol);

        // Determine BUY/SELL sequencing
        String key = portfolioId.toString() + "_" + symbol;
        String lastSide = lastSideMap.getOrDefault(key, "SELL"); // force BUY first

        String newSide = lastSide.equals("BUY") ? "SELL" : "BUY";
        t.setSide(newSide);
        lastSideMap.put(key, newSide);

        // INVALID TRADE
        if (invalidTrade) {
            t.setTradeId(UUID.randomUUID());
            t.setPricePerStock(-1 * (10 + random.nextDouble(50)));
            t.setQuantity(-1L * (1 + random.nextInt(20)));

            long randomGapSeconds = 1 + random.nextInt(300);
            lastTimestamp = lastTimestamp.plusSeconds(randomGapSeconds);
            t.setTimestamp(lastTimestamp);

            return t;
        }

        // VALID TRADE
        t.setTradeId(UUID.randomUUID());
        t.setPricePerStock(100 + random.nextDouble(101));
        t.setQuantity(1 + random.nextLong(100));

        long randomGapSeconds = 1 + random.nextInt(300);
        lastTimestamp = lastTimestamp.plusSeconds(randomGapSeconds);
        t.setTimestamp(lastTimestamp);

        return t;

    }
}