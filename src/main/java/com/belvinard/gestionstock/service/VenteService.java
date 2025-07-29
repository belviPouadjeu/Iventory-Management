package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.LigneVenteDTO;
import com.belvinard.gestionstock.dto.VenteDTO;
import com.belvinard.gestionstock.models.EtatVente;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface VenteService {

  VenteDTO findById(Long id);

  VenteDTO findByCode(String code);

  List<VenteDTO> findAll();

  void delete(Long id);

  @Transactional
  VenteDTO createVente(Long entrepriseId, VenteDTO venteDTO);

  @Transactional
  VenteDTO updateVente(Long id, VenteDTO venteDTO);

  @Transactional
  VenteDTO finalizeVente(Long idVente);

  List<VenteDTO> findAllByEntreprise(Long entrepriseId);

  List<VenteDTO> findByEtatVente(EtatVente etatVente);

  List<VenteDTO> findByEntrepriseAndEtatVente(Long entrepriseId, EtatVente etatVente);

  List<VenteDTO> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

  List<VenteDTO> findByEntrepriseAndDateRange(Long entrepriseId, LocalDateTime startDate,
                                              LocalDateTime endDate);

  List<LigneVenteDTO> findAllLignesVenteByVenteId(Long idVente);

  @Transactional
  LigneVenteDTO addLigneVente(Long venteId, LigneVenteDTO ligneVenteDTO);
}