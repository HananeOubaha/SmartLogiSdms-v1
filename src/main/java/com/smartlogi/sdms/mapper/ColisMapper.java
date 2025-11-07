package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.DTO.ColisCreationDto;
import com.smartlogi.sdms.DTO.ColisDto;
import com.smartlogi.sdms.model.Colis;
import org.mapstruct.MappingTarget;// Import nécessaire pour l'association
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;

@Mapper(componentModel = "spring")
public interface ColisMapper {

    // Conversion DTO de Création vers Entité
    @Mapping(target = "id", ignore = true) // L'ID est généré par Hibernate
    @Mapping(target = "livreur", ignore = true) // Le livreur est assigné via le service
    @Mapping(target = "statut", ignore = true) // Le statut est initialisé dans @PrePersist de l'Entité
    @Mapping(target = "historique", ignore = true) // L'historique est géré par le service
    @Mapping(target = "produits", ignore = true) // Les produits ne sont pas gérés ici
    // Mapping des IDs UUID vers les entités
    @Mapping(source = "clientExpediteurId", target = "clientExpediteur.id")
    @Mapping(source = "destinataireId", target = "destinataire.id")
    @Mapping(source = "zoneId", target = "zone.id")
    Colis toEntity(ColisCreationDto dto);


    // Conversion Entité vers DTO de Réponse
    @Mapping(source = "clientExpediteur.nom", target = "clientExpediteurNomComplet") // Exemple de mapping de nom
    @Mapping(source = "zone.nom", target = "zoneNom")
    @Mapping(expression = "java(colis.getLivreur() != null ? colis.getLivreur().getId() : null)", target = "livreurId")
    @Mapping(source = "statut", target = "statut")
    @Mapping(source = "priorite", target = "priorite")
    ColisDto toDto(Colis colis);

    List<ColisDto> toDto(List<Colis> colis);

    // Mappeur inverse pour les futures mises à jour
    @Mapping(target = "clientExpediteur", ignore = true)
    @Mapping(target = "destinataire", ignore = true)
    @Mapping(target = "zone", ignore = true)
    Colis updateEntityFromDto(ColisDto dto, @MappingTarget Colis entity);
}