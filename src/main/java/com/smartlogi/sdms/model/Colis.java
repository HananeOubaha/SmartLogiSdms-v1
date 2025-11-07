package com.smartlogi.sdms.model;

import com.smartlogi.sdms.enums.PrioriteColis;
import com.smartlogi.sdms.enums.StatutColis;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; // Ajouté pour la cohérence avec les autres entités
import org.hibernate.annotations.GenericGenerator; // Nécessaire pour la stratégie UUID
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID; // <-- NOUVEL IMPORT

/**
 * Entité représentant un colis dans le système SDMS.
 * Gère les relations clés avec les autres entités du modèle.
 */
@Entity
@Table(name = "colis")
@Data
@NoArgsConstructor
@AllArgsConstructor // Ajouté pour la cohérence
public class Colis {

    @Id
    // Générateur UUID et mapping vers la colonne VARCHAR(36)
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private UUID id; // <-- CORRECTION: Changé de Long à UUID

    private String description;
    private Double poids;

    @Enumerated(EnumType.STRING)
    private StatutColis statut;

    @Enumerated(EnumType.STRING)
    private PrioriteColis priorite;

    @Column(name = "ville_destination")
    private String villeDestination;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    // --- Relations Many-to-One ---
    // Les colonnes doivent pointer vers l'ID de type UUID dans les autres tables.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livreur_id", referencedColumnName = "id", columnDefinition = "VARCHAR(36)") // CORRECTION: Nom colonne + Type FK
    private Livreur livreur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_expediteur_id", referencedColumnName = "id", nullable = false, columnDefinition = "VARCHAR(36)") // CORRECTION: Nom colonne + Type FK
    private ClientExpéditeur clientExpediteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", referencedColumnName = "id", nullable = false, columnDefinition = "VARCHAR(36)") // CORRECTION: Nom colonne + Type FK
    private Destinataire destinataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", referencedColumnName = "id", nullable = false, columnDefinition = "VARCHAR(36)") // CORRECTION: Nom colonne + Type FK
    private Zone zone;

    // --- Relations One-to-Many / Many-to-Many ---

    // Historique (Composition)
    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoriqueLivraison> historique = new ArrayList<>();

    // Colis-Produit (Relation Many-to-Many via l'entité ColisProduit)
    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ColisProduit> produits = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        if (statut == null) {
            statut = StatutColis.CREE;
        }
    }
}