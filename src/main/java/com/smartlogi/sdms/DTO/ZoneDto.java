package com.smartlogi.sdms.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID; // <-- AJOUTER CET IMPORT

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pour la gestion des zones de livraison.") // Ajout de la description pour la clarté Swagger
public class ZoneDto {

    // CORRECTION ICI : Changé de Long à UUID
    @Schema(description = "Identifiant unique de la zone (lecture seule)", example = "a1b2c3d4-e5f6-7g8h-...")
    private UUID id;

    @NotBlank(message = "Le nom de la zone est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    @Schema(example = "Casablanca-Centre", required = true)
    private String nom;

    @Size(max = 20, message = "Le code postal ne doit pas dépasser 20 caractères")
    @Schema(example = "20000")
    private String codePostal;
}