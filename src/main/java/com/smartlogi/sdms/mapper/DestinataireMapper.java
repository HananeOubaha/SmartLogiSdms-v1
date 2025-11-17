package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.DTO.DestinataireDto;
import com.smartlogi.sdms.model.Destinataire;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DestinataireMapper {

    DestinataireDto toDto(Destinataire destinataire);
    List<DestinataireDto> toDto(List<Destinataire> destinataires);

    Destinataire toEntity(DestinataireDto destinataireDto);
}