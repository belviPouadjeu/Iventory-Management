package com.belvinard.gestionstock.repositories;

import com.belvinard.gestionstock.models.LigneCommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LigneCommandeClientRepository extends JpaRepository<LigneCommandeClient, Long> {


  List<LigneCommandeClient> findAllByCommandeClientId(Long id);

  List<LigneCommandeClient> findAllByArticleId(Long id);
}
