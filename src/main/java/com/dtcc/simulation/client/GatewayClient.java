package com.dtcc.simulation.client;

import com.dtcc.simulation.dto.PortfolioCreateResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.dtcc.simulation.dto.PortfolioCreateRequest;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GatewayClient {

    private final WebClient webClient;

    public UUID callPortfolioService(
            PortfolioCreateRequest request,
            String authorizationHeader) {

        PortfolioCreateResponse response = webClient
                .post()
                .uri("http://pms-api-gateway:8080/portfolio/api/portfolio/create")
                .header("Authorization", authorizationHeader)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PortfolioCreateResponse.class)
                .block();

        return response.getPortfolioId();
    }
}

