package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.DTO.DestinataireDto;
import com.smartlogi.sdms.model.Destinataire;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DestinataireMapper {

    DestinataireDto toDto(Destinataire destinataire);

    Destinataire toEntity(DestinataireDto destinataireDto);
}