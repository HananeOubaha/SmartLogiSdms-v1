package com.smartlogi.sdms.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID; // <-- IMPORT NÉCESSAIRE

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pour l'entité Livreur.")
public class LivreurDto {

    // CORRECTION ICI : Changé de Long à UUID
    @Schema(description = "Identifiant unique du livreur (lecture seule)", example = "a1b2c3d4-...")
    private UUID id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    @Schema(example = "Idrissi", required = true)
    private String nom;

    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    @Schema(example = "Rachid")
    private String prenom;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    @Schema(example = "0701020304", required = true)
    private String telephone;

    @NotBlank(message = "Le véhicule est obligatoire")
    @Size(max = 50, message = "Le type de véhicule ne doit pas dépasser 50 caractères")
    @Schema(example = "Camionnette", required = true)
    private String vehicule;

    @Schema(description = "Zone ou ville par défaut assignée au livreur", example = "Marrakech Gueliz")
    private String zoneAssignee;
}