package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.LivreurDto;
import com.smartlogi.sdms.mapper.LivreurMapper;
import com.smartlogi.sdms.model.Livreur;
import com.smartlogi.sdms.repository.LivreurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
// Suppression de l'import java.util.UUID

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LivreurService {

    private final LivreurRepository livreurRepository;
    private final LivreurMapper livreurMapper;

    // CREATE
    public LivreurDto createLivreur(LivreurDto livreurDto) {
        Livreur livreur = livreurMapper.toEntity(livreurDto);
        // L'ID String est généré par @PrePersist dans l'Entité
        Livreur savedLivreur = livreurRepository.save(livreur);
        return livreurMapper.toDto(savedLivreur);
    }

    // READ ALL
    public List<LivreurDto> getAllLivreurs() {
        return livreurRepository.findAll().stream()
                .map(livreurMapper::toDto)
                .collect(Collectors.toList());
    }

    // READ BY ID
    // CORRECTION : id doit être String
    public LivreurDto getLivreurById(String id) {
        Livreur livreur = livreurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livreur non trouvé avec l'ID: " + id));
        return livreurMapper.toDto(livreur);
    }

    // FETCH ENTITY (pour usage interne, ex: dans ColisService)
    // CORRECTION : id doit être String
    public Livreur getLivreurEntityById(String id) {
        return livreurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livreur non trouvé avec l'ID: " + id));
    }

    // UPDATE
    // CORRECTION : id doit être String
    public LivreurDto updateLivreur(String id, LivreurDto livreurDto) {
        Livreur existingLivreur = livreurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livreur non trouvé avec l'ID: " + id));

        // Mise à jour des champs
        existingLivreur.setNom(livreurDto.getNom());
        existingLivreur.setPrenom(livreurDto.getPrenom());
        existingLivreur.setTelephone(livreurDto.getTelephone());
        existingLivreur.setVehicule(livreurDto.getVehicule());
        existingLivreur.setZoneAssignee(livreurDto.getZoneAssignee());

        Livreur updatedLivreur = livreurRepository.save(existingLivreur);
        return livreurMapper.toDto(updatedLivreur);
    }

    // DELETE
    // CORRECTION : id doit être String
    public void deleteLivreur(String id) {
        if (!livreurRepository.existsById(id)) {
            throw new EntityNotFoundException("Livreur non trouvé avec l'ID: " + id);
        }
        livreurRepository.deleteById(id);
    }
}