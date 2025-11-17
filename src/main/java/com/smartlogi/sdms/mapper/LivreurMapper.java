package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.DTO.LivreurDto;
import com.smartlogi.sdms.model.Livreur;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LivreurMapper {

    LivreurDto toDto(Livreur livreur);
    List<LivreurDto> toDto(List<Livreur> livreurs);
    Livreur toEntity(LivreurDto livreurDto);
}