package com.belvinard.gestionstock.repositories;

import com.belvinard.gestionstock.models.LigneVente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LigneVenteRepository extends JpaRepository<LigneVente, Long> {
    List<LigneVente> findAllByVenteId(Long venteId);

    List<LigneVente> findAllByArticleId(Long articleId);

    void deleteAllByVenteId(Long venteId);
}