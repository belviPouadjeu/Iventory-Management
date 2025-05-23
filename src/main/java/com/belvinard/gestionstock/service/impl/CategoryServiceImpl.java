package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.CategoryDTO;
import com.belvinard.gestionstock.exceptions.DuplicateEntityException;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.Category;
import com.belvinard.gestionstock.models.Entreprise;
import com.belvinard.gestionstock.repositories.CategoryRepository;
import com.belvinard.gestionstock.repositories.EntrepriseRepository;
import com.belvinard.gestionstock.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;
    private final EntrepriseRepository entrepriseRepository;

    @Override
    public CategoryDTO addCategory(Long entrepriseId, CategoryDTO categoryDTO) {
        List<Category> existing = categoryRepository.findByCode(categoryDTO.getCode());

        if (!existing.isEmpty()) {
            throw new DuplicateEntityException("Une catégorie avec le code " + categoryDTO.getCode() + " existe déjà.");
        }

        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise", "id", entrepriseId));

        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setEntreprise(entreprise);

        Category savedCategory = categoryRepository.save(category);

        Category categoryWithEntreprise = categoryRepository.findByIdWithEntreprise(savedCategory.getId())
                .orElseThrow(() -> new RuntimeException("La catégorie n'a pas pu être retrouvée après la sauvegarde."));

        CategoryDTO resultDTO = modelMapper.map(categoryWithEntreprise, CategoryDTO.class);
        resultDTO.setEntrepriseName(categoryWithEntreprise.getEntreprise().getNom());
        resultDTO.setEntrepriseId(categoryWithEntreprise.getEntreprise().getId());

        return resultDTO;
    }


    @Override
    public CategoryDTO findByCode(String code) {
        Category category = categoryRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie not found with code : " + code));

        CategoryDTO dto = modelMapper.map(category, CategoryDTO.class);

        if (category.getEntreprise() != null) {
            dto.setEntrepriseName(category.getEntreprise().getNom());
        }

        return dto;
    }


    @Override
    public List<CategoryDTO> findByDesignation(String designation) {
        List<Category> categories = categoryRepository.findByDesignationContainingIgnoreCase(designation);

        if (categories.isEmpty()) {
            throw new ResourceNotFoundException("Aucune catégorie trouvée avec la désignation : " + designation);
        }

        return categories.stream()
                .map(cat -> {
                    CategoryDTO dto = modelMapper.map(cat, CategoryDTO.class);
                    if (cat.getEntreprise() != null) {
                        dto.setEntrepriseName(cat.getEntreprise().getNom());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<CategoryDTO> getAllCategoriesWithEntreprise() {
        List<Category> categories = categoryRepository.findAllWithEntreprise();

        return categories.stream().map(category -> {
            CategoryDTO categoryFromDB = modelMapper.map(category, CategoryDTO.class);

            if (category.getEntreprise() != null) {
                categoryFromDB .setEntrepriseName(category.getEntreprise().getNom());
            }

            return categoryFromDB ;
        }).collect(Collectors.toList());
    }


    @Override
    public CategoryDTO delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "not found with id", id));

        CategoryDTO deletedCategory = modelMapper.map(category, CategoryDTO.class);

        if (category.getEntreprise() != null) {
            deletedCategory .setEntrepriseName(category.getEntreprise().getNom());
            deletedCategory .setEntrepriseId(category.getEntreprise().getId());
        }

        categoryRepository.delete(category);

        return deletedCategory;
    }



}
