package com.dtcc.simulation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dtcc.simulation.client.GatewayClient;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestGatewayController {

    private final GatewayClient gatewayClient;

    @PostMapping("/test-gateway")
    public String testGateway() {
        return gatewayClient.callGatewayApi();
    }
}
