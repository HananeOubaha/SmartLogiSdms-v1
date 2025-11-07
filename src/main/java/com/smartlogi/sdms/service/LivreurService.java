package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.LivreurDto;
import com.smartlogi.sdms.mapper.LivreurMapper;
import com.smartlogi.sdms.model.Livreur;
import com.smartlogi.sdms.repository.LivreurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

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
    public LivreurDto getLivreurById(UUID id) {
        Livreur livreur = livreurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livreur non trouvé avec l'ID: " + id));
        return livreurMapper.toDto(livreur);
    }

    // FETCH ENTITY (pour usage interne, ex: dans ColisService)
    public Livreur getLivreurEntityById(UUID id) {
        return livreurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livreur non trouvé avec l'ID: " + id));
    }

    // UPDATE
    public LivreurDto updateLivreur(UUID id, LivreurDto livreurDto) {
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
    public void deleteLivreur(UUID id) {
        if (!livreurRepository.existsById(id)) {
            throw new EntityNotFoundException("Livreur non trouvé avec l'ID: " + id);
        }
        livreurRepository.deleteById(id);
    }
}