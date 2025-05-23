package com.belvinard.gestionstock.responses;

import com.belvinard.gestionstock.dto.EntrepriseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntrepriseResponse {
    List<EntrepriseDTO> content;
}
