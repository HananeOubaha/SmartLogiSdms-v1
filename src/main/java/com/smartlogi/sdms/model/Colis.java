package com.smartlogi.sdms.model;

import com.smartlogi.sdms.enums.PrioriteColis;
import com.smartlogi.sdms.enums.StatutColis;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "colis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Colis {

    @Id
    // On garde GenerationType.UUID mais on retire la logique manuelle dans @PrePersist
    // pour éviter les conflits de génération.
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;

    private String description;

    // --- CORRECTION ICI ---
    // Hibernate cherchait 'poids', mais Liquibase a créé 'poids_total'
    @Column(name = "poids_total")
    private Double poids;

    @Enumerated(EnumType.STRING)
    private StatutColis statut;

    @Enumerated(EnumType.STRING)
    private PrioriteColis priorite;

    @Column(name = "ville_destination")
    private String villeDestination;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livreur_id")
    private Livreur livreur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_expediteur_id", nullable = false)
    private ClientExpéditeur clientExpediteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Destinataire destinataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoriqueLivraison> historique = new ArrayList<>();

    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ColisProduit> produits = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        // L'ID est géré par @GeneratedValue, on ne touche qu'aux dates et statuts
        dateCreation = LocalDateTime.now();
        if (statut == null) {
            statut = StatutColis.CREE;
        }
    }
}