package com.belvinard.gestionstock.repositories;

import com.belvinard.gestionstock.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByCode(String code);

    List<Category> findByDesignationContainingIgnoreCase(String designation);

    Optional<Category> findByCodeIgnoreCase(String code);

    @Query("SELECT c FROM Category c JOIN FETCH c.entreprise")
    List<Category> findAllWithEntreprise();


    @Query("SELECT c FROM Category c JOIN FETCH c.entreprise WHERE c.id = :id")
    Optional<Category> findByIdWithEntreprise(@Param("id") Long id);

}
