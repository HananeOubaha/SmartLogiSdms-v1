package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.DTO.LivreurDto;
import com.smartlogi.sdms.model.Livreur;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LivreurMapper {

    LivreurDto toDto(Livreur livreur);

    Livreur toEntity(LivreurDto livreurDto);
}