package com.belvinard.gestionstock.repositories;


import com.belvinard.gestionstock.models.LigneCommandeFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface LigneCommandeFournisseurRepository extends JpaRepository<LigneCommandeFournisseur, Long> {

    List<LigneCommandeFournisseur> findAllByCommandeFournisseurId(Long commandeFournisseurId);

    //List<LigneCommandeFournisseur> findAllByArticleId(Long articleId);

    List<LigneCommandeFournisseur> findByArticleId(Long articleId);
    
    @Query("SELECT COALESCE(SUM(l.prixUnitaireTtc * l.quantite), 0) FROM LigneCommandeFournisseur l WHERE l.commandeFournisseur.id = :commandeFournisseurId")
    BigDecimal getTotalByCommandeFournisseurId(@Param("commandeFournisseurId") Long commandeFournisseurId);
}

