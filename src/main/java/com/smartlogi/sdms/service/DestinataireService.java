package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.DestinataireDto;
import com.smartlogi.sdms.mapper.DestinataireMapper;
import com.smartlogi.sdms.model.Destinataire;
import com.smartlogi.sdms.repository.DestinataireRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
// Suppression de l'import java.util.UUID
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DestinataireService {

    private final DestinataireRepository destinataireRepository;
    private final DestinataireMapper destinataireMapper;

    // CREATE
    public DestinataireDto createDestinataire(DestinataireDto destinataireDto) {
        Destinataire destinataire = destinataireMapper.toEntity(destinataireDto);
        // L'ID String est généré par @PrePersist dans l'Entité
        Destinataire savedDestinataire = destinataireRepository.save(destinataire);
        return destinataireMapper.toDto(savedDestinataire);
    }

    // READ ALL
    public List<DestinataireDto> getAllDestinataires() {
        return destinataireRepository.findAll().stream()
                .map(destinataireMapper::toDto)
                .collect(Collectors.toList());
    }

    // READ BY ID
    // CORRECTION : id doit être String
    public DestinataireDto getDestinataireById(String id) {
        Destinataire destinataire = destinataireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Destinataire non trouvé avec l'ID: " + id));
        return destinataireMapper.toDto(destinataire);
    }

    // FETCH ENTITY (pour usage interne, ex: dans ColisService)
    // CORRECTION : id doit être String
    public Destinataire getDestinataireEntityById(String id) {
        return destinataireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Destinataire non trouvé avec l'ID: " + id));
    }

    // UPDATE
    // CORRECTION : id doit être String
    public DestinataireDto updateDestinataire(String id, DestinataireDto destinataireDto) {
        Destinataire existingDestinataire = destinataireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Destinataire non trouvé avec l'ID: " + id));

        // Mettre à jour tous les champs du DTO
        existingDestinataire.setNom(destinataireDto.getNom());
        existingDestinataire.setPrenom(destinataireDto.getPrenom());
        existingDestinataire.setEmail(destinataireDto.getEmail());
        existingDestinataire.setTelephone(destinataireDto.getTelephone());
        existingDestinataire.setAdresse(destinataireDto.getAdresse());

        Destinataire updatedDestinataire = destinataireRepository.save(existingDestinataire);
        return destinataireMapper.toDto(updatedDestinataire);
    }

    // DELETE
    // CORRECTION : id doit être String
    public void deleteDestinataire(String id) {
        if (!destinataireRepository.existsById(id)) {
            throw new EntityNotFoundException("Destinataire non trouvé avec l'ID: " + id);
        }
        destinataireRepository.deleteById(id);
    }
}