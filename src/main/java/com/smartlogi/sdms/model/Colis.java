package com.smartlogi.sdms.model;

import com.smartlogi.sdms.enums.PrioriteColis;
import com.smartlogi.sdms.enums.StatutColis;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * Entité représentant un colis dans le système SDMS.
 * Gère les relations clés avec les autres entités du modèle.
 */
@Entity
@Table(name = "colis")
@Data
public class Colis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_livreur")
    private Livreur livreur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client_expediteur", nullable = false)
    private ClientExpéditeur clientExpediteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_destinataire", nullable = false)
    private Destinataire destinataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zone", nullable = false)
    private Zone zone;

    // --- Relations One-to-Many / Many-to-Many ---

    // Historique (Composition)
    // Le type est List<HistoriqueLivraison>, ce qui est correct pour @OneToMany
    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoriqueLivraison> historique = new ArrayList<>();

    // Colis-Produit (Relation Many-to-Many via l'entité ColisProduit)
    // Le type est List<ColisProduit>, ce qui est correct pour @OneToMany
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