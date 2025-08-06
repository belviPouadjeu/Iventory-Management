package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.ArticleDTO;
import com.belvinard.gestionstock.dto.EntrepriseDTO;
import com.belvinard.gestionstock.dto.LigneCommandeClientDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ArticleService {
    ArticleDTO createArticle(Long entrepriseId, ArticleDTO articleDTO);

    List<ArticleDTO> getAllArticles();

    ArticleDTO deleteArticle(Long id);

    /* ================== FIND ARTICLE BY ID ================== */
    ArticleDTO findAllByArticleId(Long id);

    ArticleDTO findByCodeArticle(String codeArticle);
    List<ArticleDTO> findAllArticleByIdCategory(Long idCategory);
    ArticleDTO updateArticleImage(Long id, MultipartFile image) throws IOException;
    String getPresignedImageUrl(Long id);

    //List<LigneVenteDTO> findHistoriqueVentes(Long idArticle);

    List<LigneCommandeClientDTO> findHistoriqueCommandeClient(Long idArticle);

}
