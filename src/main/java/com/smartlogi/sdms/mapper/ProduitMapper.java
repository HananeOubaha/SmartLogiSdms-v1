package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.DTO.ProduitDto;
import com.smartlogi.sdms.model.Produit;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProduitMapper {

    ProduitDto toDto(Produit produit);

    Produit toEntity(ProduitDto produitDto);

    List<ProduitDto> toDto(List<Produit> produits);
}