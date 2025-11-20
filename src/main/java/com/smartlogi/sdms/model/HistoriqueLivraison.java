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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;

    // CORRECTION: Aligné avec le schéma Liquibase
    @Column(name = "statut_precedent", length = 50)
    private String statutPrecedent;

    @Column(name = "statut_actuel", nullable = false, length = 50)
    private String statutActuel;

    @Column(name = "date_changement", nullable = false)
    private LocalDateTime dateChangement;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    // Relation ManyToOne vers Colis
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colis_id", referencedColumnName = "id", nullable = false)
    private Colis colis;

    @PrePersist
    protected void onPrePersist() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (dateChangement == null) {
            dateChangement = LocalDateTime.now();
        }
    }
}