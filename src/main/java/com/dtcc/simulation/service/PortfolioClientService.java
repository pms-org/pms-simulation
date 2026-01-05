package com.dtcc.simulation.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dtcc.simulation.dto.PortfolioCreateRequest;
import com.dtcc.simulation.dto.PortfolioCreateResponse;

@Service
public class PortfolioClientService {
 
    private final RestTemplate restTemplate;

    @Value("${portfolio.service.url}")
    private String portfolioServiceUrl;

    public PortfolioClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UUID createPortfolio(PortfolioCreateRequest request) {

        String url = portfolioServiceUrl + "/api/portfolio/create";

        PortfolioCreateResponse response =
                restTemplate.postForObject(url, request, PortfolioCreateResponse.class);

        return response.getPortfolioId();
    }
}
