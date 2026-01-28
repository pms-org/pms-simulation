package com.dtcc.simulation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dtcc.simulation.entity.Symbol;

public interface SymbolRepository extends JpaRepository<Symbol, String> {

    @Query("SELECT s.symbol FROM Symbol s")
    List<String> findAllSymbols();

}
