package com.belvinard.gestionstock.repositories;

import com.belvinard.gestionstock.models.Vente;
import com.belvinard.gestionstock.models.EtatVente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VenteRepository extends JpaRepository<Vente, Long> {
    Optional<Vente> findByCode(String code);

    List<Vente> findAllByEntrepriseId(Long entrepriseId);

    List<Vente> findAllByEtatVente(EtatVente etatVente);

    List<Vente> findAllByEntrepriseIdAndEtatVente(Long entrepriseId, EtatVente etatVente);

    List<Vente> findAllByCreationDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Vente> findAllByEntrepriseIdAndCreationDateBetween(Long entrepriseId, LocalDateTime startDate,
            LocalDateTime endDate);
}