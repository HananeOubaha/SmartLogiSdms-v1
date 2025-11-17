package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.ColisCreationDto;
import com.smartlogi.sdms.DTO.ColisDto;
import com.smartlogi.sdms.mapper.ColisMapper;
import com.smartlogi.sdms.model.*;
import com.smartlogi.sdms.enums.StatutColis;
import com.smartlogi.sdms.repository.ColisRepository;
import com.smartlogi.sdms.repository.HistoriqueLivraisonRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
// Suppression de l'import java.util.UUID car il est remplacé par String
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColisService {

    private final ColisRepository colisRepository;
    private final ColisMapper colisMapper;
    private final HistoriqueLivraisonRepository historiqueRepository;

    // Injection des services des entités liées pour la validation
    private final ClientExpéditeurService clientExpéditeurService;
    private final DestinataireService destinataireService;
    private final ZoneService zoneService;
    private final LivreurService livreurService;

    // --- Méthode de Traçabilité ---
    private void enregistrerHistorique(Colis colis, String commentaire) {
        HistoriqueLivraison historique = new HistoriqueLivraison();
        historique.setColis(colis);
        historique.setStatut(colis.getStatut().name());
        historique.setDateChangement(LocalDateTime.now());
        historique.setCommentaire(commentaire);

        historiqueRepository.save(historique);
    }

    // ============================================
    // 1. CRÉATION (Le début du flux)
    // ============================================

    @Transactional
    public ColisDto createColis(ColisCreationDto creationDto) {

        // 1. Validation de l'existence des IDs String (vérifie les FKs)
        // Les services sont censés avoir été corrigés pour accepter les String.
        ClientExpéditeur client = clientExpéditeurService.getClientEntityById(creationDto.getClientExpediteurId());
        Destinataire destinataire = destinataireService.getDestinataireEntityById(creationDto.getDestinataireId());
        Zone zone = zoneService.getZoneEntityById(creationDto.getZoneId());

        // 2. Création de l'Entité Colis et mapping
        Colis colis = colisMapper.toEntity(creationDto);

        // Assigner les entités résolues
        colis.setClientExpediteur(client);
        colis.setDestinataire(destinataire);
        colis.setZone(zone);

        // Statut initialisé à CRÉÉ dans @PrePersist
        Colis savedColis = colisRepository.save(colis);

        // 3. Enregistrement de la première étape de l'historique
        enregistrerHistorique(savedColis, "Colis créé par le client expéditeur.");

        return colisMapper.toDto(savedColis);
    }

    // ============================================
    // 2. AFFICHAGE (READ)
    // ============================================

    // CORRECTION : id doit être String
    public ColisDto getColisById(String id) {
        Colis colis = colisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Colis non trouvé avec l'ID: " + id));
        return colisMapper.toDto(colis);
    }

    public List<ColisDto> getAllColis() {
        return colisMapper.toDto(colisRepository.findAll());
    }

    // ============================================
    // 3. MISE À JOUR DU STATUT (Workflow)
    // ============================================

    @Transactional
    // CORRECTION : colisId doit être String
    public ColisDto updateStatut(String colisId, StatutColis nouveauStatut, String commentaire) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new EntityNotFoundException("Colis non trouvé avec l'ID: " + colisId));

        colis.setStatut(nouveauStatut);

        Colis updatedColis = colisRepository.save(colis);

        // Enregistrement de la nouvelle étape de l'historique
        enregistrerHistorique(updatedColis, commentaire);

        // Logique spécifique au workflow:
        if (nouveauStatut == StatutColis.COLLECTE) {
            // Mettre en place d'autres actions automatiques, si nécessaire.
        }

        return colisMapper.toDto(updatedColis);
    }

    // ============================================
    // 4. AFFECTION AU LIVREUR (Planification)
    // ============================================

    @Transactional
    // CORRECTION : colisId et livreurId doivent être String
    public ColisDto assignerLivreur(String colisId, String livreurId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new EntityNotFoundException("Colis non trouvé avec l'ID: " + colisId));

        Livreur livreur = livreurService.getLivreurEntityById(livreurId); // Validation de l'existence

        colis.setLivreur(livreur);

        // Changement de statut automatique: EN_TRANSIT ou EN_TOURNEE
        colis.setStatut(StatutColis.EN_TRANSIT);

        Colis updatedColis = colisRepository.save(colis);

        // Enregistrement de l'historique de l'affectation
        enregistrerHistorique(updatedColis, "Colis affecté au livreur: " + livreur.getNom() + " " + livreur.getPrenom() + ".");

        return colisMapper.toDto(updatedColis);
    }

    // ============================================
    // 5. SUPPRESSION (DELETE)
    // ============================================

    @Transactional
    // CORRECTION : id doit être String
    public void deleteColis(String id) {
        if (!colisRepository.existsById(id)) {
            throw new EntityNotFoundException("Colis non trouvé avec l'ID: " + id);
        }
        colisRepository.deleteById(id);
    }
}