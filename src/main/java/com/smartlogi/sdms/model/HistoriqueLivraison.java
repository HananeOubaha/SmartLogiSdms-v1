package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
// Suppression de l'import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime;
// Suppression de l'import java.util.UUID

@Entity
@Table(name = "historique_livraison")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueLivraison {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id; // <-- CORRECTION: Changé de UUID à String

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
    // Assure que la FK utilise le type VARCHAR(36) pour l'ID du Colis
    @JoinColumn(name = "colis_id", referencedColumnName = "id", nullable = false, columnDefinition = "VARCHAR(36)")
    private Colis colis;

    /**
     * Logique pour générer l'ID UUID sous forme de String AVANT l'insertion.
     */
    @PrePersist
    protected void onPrePersist() {
        if (this.id == null) {
            // Génère un UUID et le stocke comme String
            this.id = java.util.UUID.randomUUID().toString();
        }
    }
}