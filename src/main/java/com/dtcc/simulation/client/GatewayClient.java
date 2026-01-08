package com.dtcc.simulation.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GatewayClient {

    private final WebClient webClient;

    @Value("${portfolio.service.url}")
    private String portfolioServiceUrl;

    public String callGatewayApi() {

        return webClient
                .post()
                 .uri(portfolioServiceUrl + "/create")

                .attributes(
                    ServletOAuth2AuthorizedClientExchangeFilterFunction
                        .clientRegistrationId("gateway-client")
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
