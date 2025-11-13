package com.smartlogi.sdms.model;

import com.smartlogi.sdms.enums.PrioriteColis;
import com.smartlogi.sdms.enums.StatutColis;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
// Suppression de l'import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
// Suppression de l'import java.util.UUID

/**
 * Entité représentant un colis dans le système SDMS.
 * Gère les relations clés avec les autres entités du modèle.
 */
@Entity
@Table(name = "colis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Colis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id; // <-- CORRECTION: Changé de UUID à String

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
    // Les colonnes FK doivent pointer vers l'ID de type String/VARCHAR(36)

    @ManyToOne(fetch = FetchType.LAZY)
    // Pas besoin de columnDefinition si le type est String et que la PK mappée est VARCHAR(36)
    @JoinColumn(name = "livreur_id", referencedColumnName = "id")
    private Livreur livreur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_expediteur_id", referencedColumnName = "id", nullable = false)
    private ClientExpéditeur clientExpediteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", referencedColumnName = "id", nullable = false)
    private Destinataire destinataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", referencedColumnName = "id", nullable = false)
    private Zone zone;

    // --- Relations One-to-Many / Many-to-Many ---

    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoriqueLivraison> historique = new ArrayList<>();

    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ColisProduit> produits = new ArrayList<>();

    /**
     * Logique pour générer l'ID UUID sous forme de String AVANT l'insertion.
     */
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        dateCreation = LocalDateTime.now();
        if (statut == null) {
            statut = StatutColis.CREE;
        }
    }
}