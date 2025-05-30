package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.ArticleDTO;
import com.belvinard.gestionstock.dto.CategoryDTO;
import com.belvinard.gestionstock.dto.EntrepriseDTO;
import com.belvinard.gestionstock.dto.LigneCommandeClientDTO;
import com.belvinard.gestionstock.exceptions.APIException;
import com.belvinard.gestionstock.exceptions.DuplicateEntityException;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.Article;
import com.belvinard.gestionstock.models.Category;
import com.belvinard.gestionstock.models.Entreprise;
import com.belvinard.gestionstock.models.LigneCommandeClient;
import com.belvinard.gestionstock.repositories.ArticleRepository;
import com.belvinard.gestionstock.repositories.CategoryRepository;
import com.belvinard.gestionstock.repositories.EntrepriseRepository;
import com.belvinard.gestionstock.repositories.LigneCommandeClientRepository;
import com.belvinard.gestionstock.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final LigneCommandeClientRepository ligneCommandeClientRepository;
    //private final LigneVenteRepository ligneVenteRepository;

    @Override
    public ArticleDTO createArticle(Long entrepriseId, Long categoryId, ArticleDTO articleDTO) {

        List<Article> existingArticles = articleRepository.findByCodeArticleAndEntrepriseId(
                articleDTO.getCodeArticle(), entrepriseId
        );

        if (!existingArticles.isEmpty()) {
            throw new DuplicateEntityException("Article avec le code '" + articleDTO.getCodeArticle()
                    + "' existe déjà pour cette entreprise.");
        }


        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise", "id", entrepriseId));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Article article = modelMapper.map(articleDTO, Article.class);

        if (article.getQuantiteEnStock() == null) {
            article.setQuantiteEnStock(0L);
        }

        // Calcul automatique du prix TTC
        if (article.getPrixUnitaireHt() != null && article.getTauxTva() != null) {
            BigDecimal ttc = article.getPrixUnitaireHt()
                    .add(article.getPrixUnitaireHt()
                            .multiply(article.getTauxTva().divide(BigDecimal.valueOf(100))));
            article.setPrixUnitaireTtc(ttc);
        }

        article.setEntreprise(entreprise);
        article.setCategory(category);

        Article articleSaved = articleRepository.save(article);

        ArticleDTO responseDTO = modelMapper.map(articleSaved, ArticleDTO.class);
        responseDTO.setCategoryId(articleSaved.getCategory().getId());
        responseDTO.setEntrepriseId(articleSaved.getEntreprise().getId());

        responseDTO.setCategoryDesignation(articleSaved.getCategory().getDesignation());
        responseDTO.setEntrepriseName(articleSaved.getEntreprise().getNom());

        return responseDTO;

    }

    /* ================== GET ALL ARTICLES ================== */
    @Override
    public List<ArticleDTO> getAllArticles() {
        List<Article> articles = articleRepository.findAll();

        return articles.stream().map(article -> {
            ArticleDTO createdArticleDTO = modelMapper.map(article, ArticleDTO.class);

            if (article.getEntreprise() != null) {
                createdArticleDTO.setEntrepriseId(article.getEntreprise().getId());
                createdArticleDTO.setEntrepriseName(article.getEntreprise().getNom()); // ➜ juste le nom
            }

            if (article.getCategory() != null) {
                createdArticleDTO.setCategoryId(article.getCategory().getId());
                createdArticleDTO.setCategoryDesignation(article.getCategory().getDesignation()); // ➜ juste la désignation
            }

            return createdArticleDTO;
        }).collect(Collectors.toList());
    }


    /* ================== FIND ARTICLE BY ID ================== */
    @Override
    public ArticleDTO findAllByArticleId(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));

        ArticleDTO dto = modelMapper.map(article, ArticleDTO.class);

        if (article.getCategory() != null) {
            dto.setCategoryId(article.getCategory().getId());
            dto.setCategoryDesignation(article.getCategory().getDesignation()); // ✅
        }

        dto.setEntrepriseId(article.getEntreprise().getId());
        dto.setEntrepriseName(article.getEntreprise().getNom()); // ✅

        return dto;
    }


    /* ================== FIND ARTICLE BY CODE ================== */

    @Override
    public ArticleDTO findByCodeArticle(String codeArticle) {
        // Vérifie si l'article existe avec le code fourni
        Article article = articleRepository.findByCodeArticleIgnoreCase(codeArticle)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "codeArticle", codeArticle));

        // Mapping vers le DTO
        ArticleDTO dto = modelMapper.map(article, ArticleDTO.class);

        // Ajout des infos enrichies
        if (article.getCategory() != null) {
            dto.setCategoryId(article.getCategory().getId());
            dto.setCategoryDesignation(article.getCategory().getDesignation());


        }

        dto.setEntrepriseId(article.getEntreprise().getId());

        return dto;
    }

    @Override
    public ArticleDTO deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));

        checkIfArticleUsedElseThrow(id);

        ArticleDTO dto = modelMapper.map(article, ArticleDTO.class);

        if (article.getEntreprise() != null) {
            dto.setEntrepriseId(article.getEntreprise().getId());
        }

        if (article.getCategory() != null) {
            dto.setCategoryId(article.getCategory().getId());
            dto.setCategoryDesignation(article.getCategory().getDesignation());
        }


        articleRepository.delete(article);

        return dto;
    }


    private void checkIfArticleUsedElseThrow(Long idArticle) {
        List<LigneCommandeClient> ligneCommandeClients = ligneCommandeClientRepository.findAllByArticleId(idArticle);
        if (!ligneCommandeClients.isEmpty()) {
            throw new APIException("Impossible de supprimer un article déjà utilisé dans des commandes client");
        }


    }

    /* ================== FIND ARTICLE BY CATEGORY ================== */

    @Override
    public List<ArticleDTO> findAllArticleByIdCategory(Long idCategory) {

        Category category = categoryRepository.findById(idCategory)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", idCategory));


        List<Article> articles = articleRepository.findAllByCategoryId(idCategory);

        return articles.stream().map(article -> {
            ArticleDTO dto = modelMapper.map(article, ArticleDTO.class);
            dto.setCategoryId(idCategory);
            dto.setCategoryDesignation(article.getCategory().getDesignation());
            dto.setEntrepriseId(article.getEntreprise().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<LigneCommandeClientDTO> findHistoriqueCommandeClient(Long idArticle) {
        Article article = articleRepository.findById(idArticle)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", idArticle));

        List<LigneCommandeClient> lignes = ligneCommandeClientRepository.findAllByArticleId(idArticle);

        return lignes.stream()
                .map(ligne -> modelMapper.map(ligne, LigneCommandeClientDTO.class))
                .collect(Collectors.toList());
    }
}
