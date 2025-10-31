package com.smartlogi.sdms.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pour le destinataire : création, consultation et mise à jour.")
public class DestinataireDto {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    @Schema(example = "Hassani")
    private String nom;

    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    @Schema(example = "Samira")
    private String prenom;

    @Email(message = "Format d'email invalide")
    @Size(max = 150, message = "L'email ne doit pas dépasser 150 caractères")
    @Schema(example = "samira.hassani@domicile.com")
    private String email; // N'est pas @NotBlank car parfois non fourni

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    @Schema(example = "0687654321", required = true)
    private String telephone;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
    @Schema(example = "3 Avenue des Roses, Rabat", required = true)
    private String adresse;
}