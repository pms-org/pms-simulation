package com.dtcc.simulation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioCreateRequest {

    private String name;
    private Long phoneNumber;
    private String address;

    
}

