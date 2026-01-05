package com.dtcc.simulation.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtcc.simulation.dto.PortfolioCreateRequest;
import com.dtcc.simulation.entity.PortfolioId;
import com.dtcc.simulation.repository.PortfolioIdRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioManagerService {

    @Autowired
    private PortfolioClientService portfolioClientService;
    @Autowired
    private PortfolioIdRepository portfolioIdRepository;

    public UUID createAndStorePortfolio(PortfolioCreateRequest request) {

        UUID newPortfolioId = portfolioClientService.createPortfolio(request);

        PortfolioId portfolioId = new PortfolioId(newPortfolioId);
        portfolioIdRepository.save(portfolioId);

        return newPortfolioId;
    }
}
