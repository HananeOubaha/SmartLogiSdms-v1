package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.LivreurDto;
import com.smartlogi.sdms.mapper.LivreurMapper;
import com.smartlogi.sdms.model.Livreur;
import com.smartlogi.sdms.model.Zone;
import com.smartlogi.sdms.repository.LivreurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import com.smartlogi.sdms.enums.RoleName;
import com.smartlogi.sdms.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LivreurService {

    private final LivreurRepository livreurRepository;
    private final LivreurMapper livreurMapper;
    private final ZoneService zoneService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    // CREATE
    public LivreurDto createLivreur(LivreurDto livreurDto) {
        Livreur livreur = livreurMapper.toEntity(livreurDto);

        // Si une zone est spécifiée, la récupérer et l'assigner
        if (livreurDto.getZoneId() != null) {
            Zone zone = zoneService.getZoneEntityById(livreurDto.getZoneId());
            livreur.setZone(zone);
        }

        // Encodage du mot de passe pour l'entité Livreur (redondant mais cohérent avec
        // le schéma actuel)
        if (livreurDto.getPassword() != null) {
            livreur.setPassword(passwordEncoder.encode(livreurDto.getPassword()));
            // Synchronisation avec la table USERS pour l'authentification
            userService.createOrUpdateUser(livreurDto.getEmail(), livreurDto.getPassword(), RoleName.ROLE_DELIVERYMAN);
        }

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
    public LivreurDto getLivreurById(String id) {
        Livreur livreur = livreurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livreur non trouvé avec l'ID: " + id));
        return livreurMapper.toDto(livreur);
    }

    // FETCH ENTITY (pour usage interne, ex: dans ColisService)
    public Livreur getLivreurEntityById(String id) {
        return livreurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livreur non trouvé avec l'ID: " + id));
    }

    // UPDATE
    public LivreurDto updateLivreur(String id, LivreurDto livreurDto) {
        Livreur existingLivreur = livreurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livreur non trouvé avec l'ID: " + id));

        // Mise à jour des champs simples
        existingLivreur.setNom(livreurDto.getNom());
        existingLivreur.setPrenom(livreurDto.getPrenom());
        existingLivreur.setTelephone(livreurDto.getTelephone());
        existingLivreur.setVehicule(livreurDto.getVehicule());
        existingLivreur.setEmail(livreurDto.getEmail());

        // Mise à jour du mot de passe si fourni
        if (livreurDto.getPassword() != null && !livreurDto.getPassword().isEmpty()) {
            existingLivreur.setPassword(passwordEncoder.encode(livreurDto.getPassword()));
            // Synchronisation avec la table USERS
            userService.createOrUpdateUser(livreurDto.getEmail(), livreurDto.getPassword(), RoleName.ROLE_DELIVERYMAN);
        }

        // Mise à jour de la relation Zone
        if (livreurDto.getZoneId() != null) {
            Zone zone = zoneService.getZoneEntityById(livreurDto.getZoneId());
            existingLivreur.setZone(zone);
        } else {
            existingLivreur.setZone(null);
        }

        Livreur updatedLivreur = livreurRepository.save(existingLivreur);
        return livreurMapper.toDto(updatedLivreur);
    }

    // DELETE
    public void deleteLivreur(String id) {
        if (!livreurRepository.existsById(id)) {
            throw new EntityNotFoundException("Livreur non trouvé avec l'ID: " + id);
        }
        livreurRepository.deleteById(id);
    }
}