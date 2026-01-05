package com.dtcc.simulation.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dtcc.simulation.dto.PortfolioCreateRequest;
import com.dtcc.simulation.service.PortfolioManagerService;

@RestController
@RequestMapping("/simulation")
public class PortfolioSimulationController {

    private PortfolioManagerService portfolioManagerService;

    public PortfolioSimulationController(PortfolioManagerService portfolioManagerService) {
        this.portfolioManagerService = portfolioManagerService;
    }

    @PostMapping("/create-portfolio")
    public UUID createPortfolio(@RequestBody PortfolioCreateRequest request) {
        return portfolioManagerService.createAndStorePortfolio(request);
    }
}
