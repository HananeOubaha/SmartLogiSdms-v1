package com.smartlogi.sdms.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Suppression de l'import java.util.UUID

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pour l'expéditeur : création, consultation et mise à jour.")
public class ClientExpéditeurDto {

    // CORRECTION ICI : Changé de UUID à String
    @Schema(description = "Identifiant unique de l'expéditeur (lecture seule)", example = "a1b2c3d4-e5f6-7g8h-...")
    private String id; // <-- CHANGÉ DE UUID À STRING

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    @Schema(example = "Akermi")
    private String nom;

    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    @Schema(example = "Youssef")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Size(max = 150, message = "L'email ne doit pas dépasser 150 caractères")
    @Schema(example = "youssef.akermi@example.com", required = true)
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    @Schema(example = "0612345678")
    private String telephone;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
    @Schema(example = "12 Rue des Oliviers, Casablanca")
    private String adresse;
}