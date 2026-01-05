package com.dtcc.simulation.client;

import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GatewayClient {

    private final WebClient webClient;

    public String callGatewayApi() {

        return webClient
                .post()
                 .uri("http://pms-api-gateway:8080/simulation/create")

                .attributes(
                    ServletOAuth2AuthorizedClientExchangeFilterFunction
                        .clientRegistrationId("gateway-client")
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
