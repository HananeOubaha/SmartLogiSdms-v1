package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.ProduitDto;
import com.smartlogi.sdms.mapper.ProduitMapper;
import com.smartlogi.sdms.model.Produit;
import com.smartlogi.sdms.repository.ProduitRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper;

    // CREATE
    public ProduitDto createProduit(ProduitDto produitDto) {
        Produit produit = produitMapper.toEntity(produitDto);
        Produit savedProduit = produitRepository.save(produit);
        return produitMapper.toDto(savedProduit);
    }

    // READ ALL
    public List<ProduitDto> getAllProduits() {
        return produitRepository.findAll().stream()
                .map(produitMapper::toDto)
                .collect(Collectors.toList());
    }

    // READ BY ID
    public ProduitDto getProduitById(String id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé avec l'ID: " + id));
        return produitMapper.toDto(produit);
    }

    // FETCH ENTITY (pour usage interne)
    public Produit getProduitEntityById(String id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé avec l'ID: " + id));
    }

    // UPDATE
    public ProduitDto updateProduit(String id, ProduitDto produitDto) {
        Produit existingProduit = produitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé avec l'ID: " + id));

        existingProduit.setNom(produitDto.getNom());
        existingProduit.setCategorie(produitDto.getCategorie());
        existingProduit.setPoids(produitDto.getPoids());
        existingProduit.setPrix(produitDto.getPrix());

        Produit updatedProduit = produitRepository.save(existingProduit);
        return produitMapper.toDto(updatedProduit);
    }

    // DELETE
    public void deleteProduit(String id) {
        if (!produitRepository.existsById(id)) {
            throw new EntityNotFoundException("Produit non trouvé avec l'ID: " + id);
        }
        produitRepository.deleteById(id);
    }
}