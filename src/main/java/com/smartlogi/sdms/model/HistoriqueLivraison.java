package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator; // Nécessaire pour la stratégie UUID

import java.time.LocalDateTime;
import java.util.UUID; // <-- Import nécessaire

@Entity
@Table(name = "historique_livraison")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueLivraison {

    @Id
    // Génère l'ID en utilisant la stratégie UUID2 d'Hibernate
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    // Mappe l'objet Java UUID vers la colonne VARCHAR(36) de la base de données
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private UUID id; // <-- CORRECTION: Changé de Long à UUID

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
    // CORRECTION: Assure que la FK utilise le type VARCHAR(36) pour l'ID du Colis
    @JoinColumn(name = "colis_id", referencedColumnName = "id", nullable = false, columnDefinition = "VARCHAR(36)")
    private Colis colis;
}