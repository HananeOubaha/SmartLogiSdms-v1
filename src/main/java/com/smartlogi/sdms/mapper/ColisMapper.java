package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.DTO.ColisCreationDto;
import com.smartlogi.sdms.DTO.ColisDto;
import com.smartlogi.sdms.model.Colis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ColisMapper {

    // ==========================================================
    // 1. Conversion DTO Création -> Entité
    // ==========================================================
    @Mapping(target = "id", ignore = true) // Généré par la base
    @Mapping(target = "livreur", ignore = true) // Assigné plus tard
    @Mapping(target = "statut", ignore = true) // Initialisé par défaut
    @Mapping(target = "historique", ignore = true) // Géré par service
    @Mapping(target = "produits", ignore = true)
    // Mapping des IDs vers les objets liés
    @Mapping(source = "clientExpediteurId", target = "clientExpediteur.id")
    @Mapping(source = "destinataireId", target = "destinataire.id")
    @Mapping(source = "zoneId", target = "zone.id")
    Colis toEntity(ColisCreationDto dto);


    // ==========================================================
    // 2. Conversion Entité -> DTO Réponse (LE FIX EST ICI)
    // ==========================================================

    //  Correction: On utilise \" au lieu de ' pour les Strings Java
    @Mapping(target = "clientExpediteurNomComplet",
            expression = "java(colis.getClientExpediteur() != null ? colis.getClientExpediteur().getNom() + \" \" + colis.getClientExpediteur().getPrenom() : \"Inconnu\")")

    //  Correction: \"Non définie\" au lieu de 'Non définie'
    @Mapping(target = "zoneNom",
            expression = "java(colis.getZone() != null ? colis.getZone().getNom() : \"Non définie\")")

    @Mapping(target = "livreurId",
            expression = "java(colis.getLivreur() != null ? colis.getLivreur().getId() : null)")

    @Mapping(source = "statut", target = "statut")
    @Mapping(source = "priorite", target = "priorite")
    ColisDto toDto(Colis colis);

    List<ColisDto> toDto(List<Colis> colis);

    // ==========================================================
    // 3. Mise à jour Entité depuis DTO
    // ==========================================================
    @Mapping(target = "clientExpediteur", ignore = true)
    @Mapping(target = "destinataire", ignore = true)
    @Mapping(target = "zone", ignore = true)
    Colis updateEntityFromDto(ColisDto dto, @MappingTarget Colis entity);
}