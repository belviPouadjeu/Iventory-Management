package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.VenteDTO;
import com.belvinard.gestionstock.dto.LigneVenteDTO;
import com.belvinard.gestionstock.exceptions.*;
import com.belvinard.gestionstock.models.*;
import com.belvinard.gestionstock.repositories.*;
import com.belvinard.gestionstock.service.VenteService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VenteServiceImpl implements VenteService {

    private final VenteRepository venteRepository;
    private final LigneVenteRepository ligneVenteRepository;
    private final ArticleRepository articleRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final CommandeClientRepository commandeClientRepository;
    private final ModelMapper modelMapper;

    @Override
    public VenteDTO findById(Long id) {
        Vente vente = venteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vente", "id", id));
        return modelMapper.map(vente, VenteDTO.class);
    }

    @Override
    public VenteDTO findByCode(String code) {
        Vente vente = venteRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Vente", "code", code));
        return modelMapper.map(vente, VenteDTO.class);
    }

    @Override
    public List<VenteDTO> findAll() {
        return venteRepository.findAll().stream()
                .map(vente -> modelMapper.map(vente, VenteDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Vente vente = venteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vente", "id", id));
        if (vente.getEtatVente() == EtatVente.FINALISEE) {
            throw new InvalidOperationException("Impossible de supprimer une vente finalisée");
        }
        // Delete all line items first
        ligneVenteRepository.deleteAllByVenteId(id);
        venteRepository.delete(vente);
    }

    // --- Business Operations ---

    @Override
    @Transactional
    public VenteDTO createVente(Long entrepriseId, VenteDTO venteDTO) {
        // Check for duplicate by code
        if (venteRepository.findAllByEntrepriseId(entrepriseId).stream()
                .anyMatch(v -> v.getCode().equals(venteDTO.getCode()))) {
            throw new APIException("Une vente avec le code '" + venteDTO.getCode() + "' existe déjà");
        }

        // Verify enterprise exists
        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise", "id", entrepriseId));

        // Map DTO to entity
        Vente vente = modelMapper.map(venteDTO, Vente.class);
        vente.setEntreprise(entreprise);
        vente.setCreationDate(LocalDateTime.now());
        vente.setEtatVente(EtatVente.EN_COURS);

        // Save and return
        Vente saved = venteRepository.save(vente);
        VenteDTO result = modelMapper.map(saved, VenteDTO.class);
        result.setEntrepriseName(entreprise.getNom());
        return result;
    }

    @Override
    @Transactional
    public VenteDTO updateVente(Long id, VenteDTO venteDTO) {
        Vente vente = venteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vente", "id", id));
        if (vente.getEtatVente() == EtatVente.FINALISEE) {
            throw new InvalidOperationException("Impossible de modifier une vente finalisée");
        }
        vente.setCode(venteDTO.getCode());
        vente.setCommentaire(venteDTO.getCommentaire());
        Vente updated = venteRepository.save(vente);
        return modelMapper.map(updated, VenteDTO.class);
    }

    @Transactional
    public VenteDTO updateEtatVente(Long idVente, EtatVente etatVente) {
        Vente vente = venteRepository.findById(idVente)
                .orElseThrow(() -> new ResourceNotFoundException("Vente", "id", idVente));
        vente.setEtatVente(etatVente);
        Vente updated = venteRepository.save(vente);
        return modelMapper.map(updated, VenteDTO.class);
    }

    @Transactional
    @Override
    public VenteDTO finalizeVente(Long idVente) {
        Vente vente = venteRepository.findById(idVente)
                .orElseThrow(() -> new ResourceNotFoundException("Vente", "id", idVente));
        if (vente.getEtatVente() == EtatVente.FINALISEE) {
            throw new InvalidOperationException("Vente déjà finalisée");
        }
        List<LigneVente> lignes = ligneVenteRepository.findAllByVenteId(idVente);
        if (lignes.isEmpty()) {
            throw new InvalidOperationException("Impossible de finaliser une vente sans lignes");
        }
        vente.setEtatVente(EtatVente.FINALISEE);
        venteRepository.save(vente);
        // Update stock for each article
        for (LigneVente ligne : lignes) {
            Article article = ligne.getArticle();
            if (article.getQuantiteEnStock() < ligne.getQuantite().longValue()) {
                throw new APIException("Stock insuffisant pour l'article: " + article.getDesignation());
            }
            article.setQuantiteEnStock(article.getQuantiteEnStock() - ligne.getQuantite().longValue());
            articleRepository.save(article);
        }
        return modelMapper.map(vente, VenteDTO.class);
    }

    // --- Search and Filter Operations ---

    @Override
    public List<VenteDTO> findAllByEntreprise(Long entrepriseId) {
        return venteRepository.findAllByEntrepriseId(entrepriseId).stream()
                .map(vente -> modelMapper.map(vente, VenteDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<VenteDTO> findByEtatVente(EtatVente etatVente) {
        return venteRepository.findAllByEtatVente(etatVente).stream()
                .map(vente -> modelMapper.map(vente, VenteDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<VenteDTO> findByEntrepriseAndEtatVente(Long entrepriseId, EtatVente etatVente) {
        return venteRepository.findAllByEntrepriseIdAndEtatVente(entrepriseId, etatVente).stream()
                .map(vente -> modelMapper.map(vente, VenteDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<VenteDTO> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return venteRepository.findAllByCreationDateBetween(startDate, endDate).stream()
                .map(vente -> modelMapper.map(vente, VenteDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<VenteDTO> findByEntrepriseAndDateRange(Long entrepriseId, LocalDateTime startDate,
            LocalDateTime endDate) {
        return venteRepository.findAllByEntrepriseIdAndCreationDateBetween(entrepriseId, startDate, endDate).stream()
                .map(vente -> modelMapper.map(vente, VenteDTO.class))
                .collect(Collectors.toList());
    }

    // --- Line Items Management ---

    @Override
    public List<LigneVenteDTO> findAllLignesVenteByVenteId(Long idVente) {
        return ligneVenteRepository.findAllByVenteId(idVente).stream()
                .map(ligne -> modelMapper.map(ligne, LigneVenteDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public LigneVenteDTO addLigneVente(Long venteId, LigneVenteDTO ligneVenteDTO) {
        // Récupérer la vente
        Vente vente = venteRepository.findById(venteId)
                .orElseThrow(() -> new ResourceNotFoundException("Vente", "id", venteId));

        // Vérifier que la vente n'est pas finalisée
        if (vente.getEtatVente() == EtatVente.FINALISEE) {
            throw new InvalidOperationException("Impossible d'ajouter une ligne à une vente finalisée");
        }

        // Récupérer l'article
        Article article = articleRepository.findById(ligneVenteDTO.getIdArticle())
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", ligneVenteDTO.getIdArticle()));

        // Vérifier le stock
        if (BigDecimal.valueOf(article.getQuantiteEnStock()).compareTo(ligneVenteDTO.getQuantite()) < 0) {
            throw new APIException("Stock insuffisant pour l'article: " + article.getDesignation());
        }

        // Mapper DTO vers entité
        LigneVente ligne = modelMapper.map(ligneVenteDTO, LigneVente.class);
        ligne.setVente(vente);
        ligne.setArticle(article);

        // Enregistrer la ligne de vente
        LigneVente saved = ligneVenteRepository.save(ligne);

        // Retourner le résultat en DTO
        return modelMapper.map(saved, LigneVenteDTO.class);
    }

    @Override
    @Transactional
    public VenteDTO createVenteFromCommande(Long commandeClientId) {
        // 1. Récupérer la commande client
        CommandeClient commande = commandeClientRepository.findById(commandeClientId)
                .orElseThrow(() -> new ResourceNotFoundException("CommandeClient", "id", commandeClientId));

        // 2. Validation : La commande doit être VALIDEE
        if (commande.getEtatCommande() != EtatCommande.VALIDEE) {
            throw new BusinessRuleException(
                    "Seules les commandes à l'état VALIDEE peuvent être transformées en vente. " +
                            "État actuel de la commande : " + commande.getEtatCommande());
        }

        // 3. Vérifier qu'il y a des lignes de commande
        if (commande.getLigneCommandeClients() == null || commande.getLigneCommandeClients().isEmpty()) {
            throw new BusinessRuleException(
                    "Impossible de créer une vente à partir d'une commande sans lignes de commande.");
        }

        // 4. Créer la vente
        Vente vente = new Vente();
        vente.setCode("VTE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        vente.setDateVente(LocalDateTime.now());
        vente.setClient(commande.getClient());
        vente.setEntreprise(commande.getEntreprise());
        vente.setCommentaire("Vente générée depuis la commande " + commande.getCode());
        vente.setEtatVente(EtatVente.EN_COURS); // État initial de la vente

        Vente savedVente = venteRepository.save(vente);

        // 5. Créer les lignes de vente depuis les lignes de commande
        for (LigneCommandeClient ligneCommande : commande.getLigneCommandeClients()) {
            LigneVente ligneVente = new LigneVente();
            ligneVente.setVente(savedVente);
            ligneVente.setArticle(ligneCommande.getArticle());
            ligneVente.setQuantite(ligneCommande.getQuantite());
            ligneVente.setPrixUnitaireHt(ligneCommande.getPrixUnitaireHt());
            ligneVente.setTauxTva(ligneCommande.getTauxTva());
            ligneVente.setPrixUnitaireTtc(ligneCommande.getPrixUnitaireTtc());

            ligneVenteRepository.save(ligneVente);
        }

        // 6. Passer la commande à l'état LIVREE
        commande.setEtatCommande(EtatCommande.LIVREE);
        commandeClientRepository.save(commande);

        // 7. Convertir et retourner le DTO
        VenteDTO venteDTO = modelMapper.map(savedVente, VenteDTO.class);

        // Enrichir avec les informations du client et de l'entreprise
        venteDTO.setClientId(savedVente.getClient().getId());
        venteDTO.setClientName(savedVente.getClient().getNom() + " " + savedVente.getClient().getPrenom());
        venteDTO.setEntrepriseId(savedVente.getEntreprise().getId());
        venteDTO.setEntrepriseName(savedVente.getEntreprise().getNom());

        return venteDTO;
    }

}
