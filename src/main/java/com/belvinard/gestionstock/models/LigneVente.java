package com.belvinard.gestionstock.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
public class LigneVente extends AbstractEntity {

    @NotNull(message = "La quantité est obligatoire")
    @DecimalMin(value = "0.01", message = "La quantité doit être supérieure à zéro")
    private BigDecimal quantite;

    @NotNull(message = "Le prix unitaire HT est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix unitaire HT doit être supérieur à zéro")
    private BigDecimal prixUnitaireHt;

    @DecimalMin(value = "0.0", message = "Le taux de TVA doit être positif")
    private BigDecimal tauxTva;

    @NotNull(message = "Le prix unitaire TTC est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix unitaire TTC doit être supérieur à zéro")
    private BigDecimal prixUnitaireTtc;

    @NotNull(message = "La vente est obligatoire")
    @ManyToOne
    @JoinColumn(name = "idvente")
    private Vente vente;

    @NotNull(message = "L'article est obligatoire")
    @ManyToOne
    @JoinColumn(name = "idarticle")
    private Article article;

}
