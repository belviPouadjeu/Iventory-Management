package com.belvinard.gestionstock.repositories;

import com.belvinard.gestionstock.models.Article;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

  Optional<Article> findArticleByCodeArticle(String codeArticle);

  List<Article> findAllByCategoryId(Long idCategory);

  Optional<Article> findByCodeArticleIgnoreCase(String codeArticle);


  List<Article> findByCodeArticleAndEntrepriseId(String codeArticle, Long entrepriseId);
}