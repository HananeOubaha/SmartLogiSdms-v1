package com.smartlogi.sdms.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

// Suppression de l'import java.util.UUID

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pour la gestion des zones de livraison.")
public class ZoneDto {

    // CORRECTION ICI : Changé de UUID à String
    @Schema(description = "Identifiant unique de la zone (lecture seule)", example = "a1b2c3d4-e5f6-7g8h-...")
    private String id; // <-- CHANGÉ DE UUID À STRING

    @NotBlank(message = "Le nom de la zone est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    @Schema(example = "Casablanca-Centre", required = true)
    private String nom;

    @Size(max = 20, message = "Le code postal ne doit pas dépasser 20 caractères")
    @Schema(example = "20000")
    private String codePostal;
}