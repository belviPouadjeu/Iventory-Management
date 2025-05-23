package com.belvinard.gestionstock.responses;


import com.belvinard.gestionstock.dto.VenteDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenteResponse {
    List<VenteDTO> content;
}
