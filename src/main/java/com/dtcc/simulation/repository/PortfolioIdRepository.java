package com.dtcc.simulation.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dtcc.simulation.entity.PortfolioId;

public interface PortfolioIdRepository extends JpaRepository<PortfolioId, UUID> {

    @Query("SELECT p.portfolio_id FROM PortfolioId p")
    List<UUID> findAllPortfolioIds();

}
