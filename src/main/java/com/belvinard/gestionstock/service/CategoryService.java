package com.belvinard.gestionstock.service;



import com.belvinard.gestionstock.dto.CategoryDTO;
import com.belvinard.gestionstock.responses.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryDTO addCategory(Long id, CategoryDTO categoryDTO);
    List<CategoryDTO> findByDesignation(String designation);


    List<CategoryDTO> getAllCategoriesWithEntreprise();


    CategoryDTO findByCode(String code);


    CategoryDTO delete(Long id);

}