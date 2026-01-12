package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.DTO.LivreurDto;
import com.smartlogi.sdms.model.Livreur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LivreurMapper {

    @Mapping(source = "zone.id", target = "zoneId")
    LivreurDto toDto(Livreur livreur);

    List<LivreurDto> toDto(List<Livreur> livreurs);

    @Mapping(target = "zone", ignore = true)
    @Mapping(target = "colisAssignes", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    Livreur toEntity(LivreurDto livreurDto);
}