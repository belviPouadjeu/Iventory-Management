package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.ArticleDTO;
import com.belvinard.gestionstock.dto.LigneCommandeClientDTO;

import java.util.List;

public interface ArticleService {
    ArticleDTO createArticle(Long entrepriseId, Long categoryId, ArticleDTO articleDTO);

    List<ArticleDTO> getAllArticles();

    ArticleDTO deleteArticle(Long id);

    /* ================== FIND ARTICLE BY ID ================== */
    ArticleDTO findAllByArticleId(Long id);

    ArticleDTO findByCodeArticle(String codeArticle);
    List<ArticleDTO> findAllArticleByIdCategory(Long idCategory);

    //List<LigneVenteDTO> findHistoriqueVentes(Long idArticle);

    List<LigneCommandeClientDTO> findHistoriaueCommandeClient(Long idArticle);

}
