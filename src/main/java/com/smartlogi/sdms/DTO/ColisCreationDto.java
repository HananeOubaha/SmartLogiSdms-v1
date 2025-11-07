package com.smartlogi.sdms.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO utilisé pour créer une nouvelle demande de colis (par le Client Expéditeur).")
public class ColisCreationDto {

    @NotBlank(message = "La description est obligatoire")
    @Schema(example = "Documents importants + 2 livres")
    private String description;

    @NotNull(message = "Le poids est obligatoire")
    @DecimalMin(value = "0.01", message = "Le poids doit être supérieur à zéro")
    @Schema(example = "1.5")
    private Double poids;

    @NotBlank(message = "La ville de destination est obligatoire")
    @Schema(example = "Marrakech")
    private String villeDestination;

    @Schema(description = "Priorité du colis (ex: URGENTE, HAUTE)", example = "NORMALE")
    private String priorite;

    // --- UUIDs des Entités Parentes ---

    @NotNull(message = "L'ID du client expéditeur est obligatoire")
    @Schema(description = "ID du Client Expéditeur (UUID)", required = true)
    private UUID clientExpediteurId;

    @NotNull(message = "L'ID du destinataire est obligatoire")
    @Schema(description = "ID du Destinataire (UUID)", required = true)
    private UUID destinataireId;

    @NotNull(message = "L'ID de la zone est obligatoire")
    @Schema(description = "ID de la Zone de destination (UUID)", required = true)
    private UUID zoneId;

    // Pour simplifier cette étape, nous n'incluons pas les produits dans le DTO de création initial.
}