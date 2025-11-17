package com.smartlogi.sdms.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pour l'entité Produit.")
public class ProduitDto {

    private String id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Schema(example = "Smartphone")
    private String nom;

    @Schema(example = "Électronique")
    private String categorie;

    @NotNull(message = "Le poids est obligatoire")
    @DecimalMin(value = "0.001", message = "Le poids doit être positif")
    @Schema(example = "0.350")
    private Double poids;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix doit être positif")
    @Schema(example = "3500.00")
    private Double prix;
}