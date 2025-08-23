package com.belvinard.gestionstock.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class EntreeStockRequest {
    @NotNull
    private Long articleId;
    
    @NotNull
    @Positive
    private BigDecimal quantite;
    
    @NotNull
    private Long entrepriseId;
}