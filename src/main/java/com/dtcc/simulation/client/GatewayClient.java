package com.dtcc.simulation.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.dtcc.simulation.dto.PortfolioCreateRequest;
import com.dtcc.simulation.dto.PortfolioCreateResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GatewayClient {

    private final WebClient webClient;

    @Value("${portfolio.service.base-url}")
    private String portfolioServiceBaseUrl;

    public UUID callPortfolioService(
            PortfolioCreateRequest request,
            String authorizationHeader) {

        PortfolioCreateResponse response = webClient
                .post()
                .uri(portfolioServiceBaseUrl + "/api/portfolio/create")
                .header("Authorization", authorizationHeader)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PortfolioCreateResponse.class)
                .block();

        return response.getPortfolioId();
    }
}
