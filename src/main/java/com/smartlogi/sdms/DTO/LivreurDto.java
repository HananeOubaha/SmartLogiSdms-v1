package com.smartlogi.sdms.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// Suppression de l'import java.util.UUID

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pour l'entité Livreur.")
public class LivreurDto {

    // CORRECTION ICI : Changé de UUID à String
    @Schema(description = "Identifiant unique du livreur (lecture seule)", example = "a1b2c3d4-e5f6-7g8h-...")
    private String id; // <-- CHANGÉ DE UUID À STRING

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