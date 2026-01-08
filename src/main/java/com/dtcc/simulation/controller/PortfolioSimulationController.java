package com.dtcc.simulation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.dtcc.simulation.dto.PortfolioCreateRequest;
import com.dtcc.simulation.dto.PortfolioCreateResponse;
import com.dtcc.simulation.service.PortfolioManagerService;

import java.util.UUID;

@RestController
@RequestMapping("/simulation")
@RequiredArgsConstructor
public class PortfolioSimulationController {

    private final PortfolioManagerService portfolioManagerService;

    @PostMapping("/create-portfolio")
    public PortfolioCreateResponse createPortfolio(
            @RequestBody PortfolioCreateRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {

        UUID portfolioId =
                portfolioManagerService.createAndStorePortfolio(request, authorizationHeader);

        return new PortfolioCreateResponse(portfolioId);
    }
}
