package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.DTO.ClientExpéditeurDto;
import com.smartlogi.sdms.model.ClientExpéditeur;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientExpéditeurMapper {

    ClientExpéditeurDto toDto(ClientExpéditeur clientExpéditeur);

    ClientExpéditeur toEntity(ClientExpéditeurDto clientExpéditeurDto);
}