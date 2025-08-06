package com.belvinard.gestionstock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDTO {

    private Long id;

    private String fileName;

    private String url;

    private String contentType;

    private Long size;

    private String message; // Optionnel pour les réponses de succès
}
