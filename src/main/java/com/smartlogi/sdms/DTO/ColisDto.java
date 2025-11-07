package com.smartlogi.sdms.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de réponse pour afficher le colis et son état.")
public class ColisDto {

    private UUID id;
    private String description;
    private Double poids;
    private String statut; // Type String pour afficher l'Enum
    private String priorite;
    private String villeDestination;
    private LocalDateTime dateCreation;

    // Simplification des IDs des relations pour la réponse DTO
    @Schema(description = "ID du Livreur assigné (peut être null)")
    private UUID livreurId;

    @Schema(description = "Nom complet de l'Expéditeur")
    private String clientExpediteurNomComplet;

    @Schema(description = "Nom de la Zone de destination")
    private String zoneNom;
}