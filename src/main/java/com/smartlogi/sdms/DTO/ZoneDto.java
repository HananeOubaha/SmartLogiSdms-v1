package com.smartlogi.sdms.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoneDto {

    private Long id; // ID pour les opérations d'UPDATE/DELETE

    @NotBlank(message = "Le nom de la zone est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @Size(max = 20, message = "Le code postal ne doit pas dépasser 20 caractères")
    private String codePostal;
}