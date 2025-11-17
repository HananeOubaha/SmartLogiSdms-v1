package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.DTO.ZoneDto;
import com.smartlogi.sdms.model.Zone;
import org.mapstruct.Mapper;
import java.util.List;

/**
 * Interface MapStruct pour la conversion entre Zone et ZoneDto.
 */
@Mapper(componentModel = "spring") // componentModel="spring" rend ce mapper injectable (via @Autowired)
public interface ZoneMapper {

    /**
     * Convertit une entité Zone en ZoneDto.
     * @param zone L'entité à convertir.
     * @return Le DTO correspondant.
     */
    ZoneDto toDto(Zone zone);
    List<ZoneDto> toDto(List<Zone> zones);
    /**
     * Convertit un ZoneDto en entité Zone.
     * @param zoneDto Le DTO à convertir.
     * @return L'entité correspondante.
     */
    Zone toEntity(ZoneDto zoneDto);
}