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

@Service
@RequiredArgsConstructor
public class ColisService {

    private final ColisRepository colisRepository;
    private final ColisMapper colisMapper;
    private final HistoriqueLivraisonRepository historiqueRepository;

    private final ClientExpéditeurService clientExpéditeurService;
    private final DestinataireService destinataireService;
    private final ZoneService zoneService;
    private final LivreurService livreurService;

    // ... (Méthodes privées d'historique inchangées) ...
    private void enregistrerHistorique(Colis colis, String commentaire) {
        enregistrerHistorique(colis, null, colis.getStatut(), commentaire);
    }

    private void enregistrerHistorique(Colis colis, StatutColis statutPrecedent, StatutColis statutActuel,
                                       String commentaire) {
        HistoriqueLivraison historique = new HistoriqueLivraison();
        historique.setColis(colis);
        historique.setStatutPrecedent(statutPrecedent != null ? statutPrecedent.name() : null);
        historique.setStatutActuel(statutActuel.name());
        historique.setDateChangement(LocalDateTime.now());
        historique.setCommentaire(commentaire);

        historiqueRepository.save(historique);
    }

    // ============================================
    // 1. CRÉATION
    // ============================================
    @Transactional
    public ColisDto createColis(ColisCreationDto creationDto) {
        ClientExpéditeur client = clientExpéditeurService.getClientEntityById(creationDto.getClientExpediteurId());
        Destinataire destinataire = destinataireService.getDestinataireEntityById(creationDto.getDestinataireId());
        Zone zone = zoneService.getZoneEntityById(creationDto.getZoneId());

        Colis colis = colisMapper.toEntity(creationDto);
        colis.setClientExpediteur(client);
        colis.setDestinataire(destinataire);
        colis.setZone(zone);

        Colis savedColis = colisRepository.save(colis);
        enregistrerHistorique(savedColis, "Colis créé par le client expéditeur.");

        return colisMapper.toDto(savedColis);
    }

    // ============================================
    // 2. AFFICHAGE (READ)
    // ============================================

    public ColisDto getColisById(String id) {
        Colis colis = colisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Colis non trouvé avec l'ID: " + id));
        return colisMapper.toDto(colis);
    }

    @Transactional(readOnly = true)
    public List<ColisDto> getAllColis() {
        return colisMapper.toDto(colisRepository.findAll());
    }

    // 👇 CORRECTION ICI : Changement de Long à String pour correspondre à l'erreur 👇
    @Transactional(readOnly = true)
    public List<ColisDto> getColisByClientId(String clientId) {
        // Le repository doit aussi avoir la signature findByClientExpediteur_Id(String id)
        return colisMapper.toDto(colisRepository.findByClientExpediteur_Id(clientId));
    }

    // ============================================
    // 3. MISE À JOUR & 4. AFFECTATION & 5. DELETE
    // ============================================

    @Transactional
    public ColisDto updateStatut(String colisId, StatutColis nouveauStatut, String commentaire) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new EntityNotFoundException("Colis non trouvé"));
        StatutColis ancienStatut = colis.getStatut();
        colis.setStatut(nouveauStatut);
        Colis updatedColis = colisRepository.save(colis);
        enregistrerHistorique(updatedColis, ancienStatut, nouveauStatut, commentaire);
        return colisMapper.toDto(updatedColis);
    }

    @Transactional
    public ColisDto assignerLivreur(String colisId, String livreurId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new EntityNotFoundException("Colis non trouvé"));
        Livreur livreur = livreurService.getLivreurEntityById(livreurId);
        colis.setLivreur(livreur);
        StatutColis ancienStatut = colis.getStatut();
        colis.setStatut(StatutColis.EN_TRANSIT);
        Colis updatedColis = colisRepository.save(colis);
        enregistrerHistorique(updatedColis, ancienStatut, StatutColis.EN_TRANSIT, "Affecté au livreur: " + livreur.getNom());
        return colisMapper.toDto(updatedColis);
    }

    @Transactional
    public void deleteColis(String id) {
        if (!colisRepository.existsById(id)) {
            throw new EntityNotFoundException("Colis non trouvé avec l'ID: " + id);
        }
        colisRepository.deleteById(id);
    }
}