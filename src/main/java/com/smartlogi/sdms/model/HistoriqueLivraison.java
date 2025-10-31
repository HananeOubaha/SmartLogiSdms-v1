package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_livraison")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueLivraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Colonne pour le statut du colis (ex: EN_PREPARATION, EN_TRANSIT, LIVRE)
    @Column(name = "statut", nullable = false, length = 50)
    private String statut;

    // Date et heure du changement de statut
    @Column(name = "date_changement", nullable = false)
    private LocalDateTime dateChangement;

    // Commentaire optionnel (ex: retard dû à la météo)
    @Column(name = "commentaire", length = 255)
    private String commentaire;

    // --- RELATION JPA ---

    // HistoriqueLivraison est l'entité Many (plusieurs historiques pour un colis)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_colis", nullable = false)
    private Colis colis;
}