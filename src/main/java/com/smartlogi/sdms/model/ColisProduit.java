package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID; // <-- Import nécessaire

@Entity
@Table(name = "colis_produit")
@Data
@NoArgsConstructor
public class ColisProduit {

    // Utilisation de la clé primaire composée
    @EmbeddedId
    private ColisProduitId id;

    // Mappe l'ID du Colis
    // La colonne FK est déjà définie dans ColisProduitId
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("colisId")
    @JoinColumn(name = "colis_id") // La colonne FK s'appelle colis_id (voir Liquibase)
    private Colis colis;

    // Mappe l'ID du Produit
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("produitId")
    @JoinColumn(name = "produit_id") // La colonne FK s'appelle produit_id (voir Liquibase)
    private Produit produit;

    private Integer quantite;

    @Column(name = "prix_unitaire")
    private Double prixUnitaire; // Le type est Double car il représente une valeur monétaire

    @Column(name = "date_ajout")
    private LocalDateTime dateAjout;

    /**
     * Constructeur utilitaire pour la création.
     * Initialise la clé composée avec les UUIDs des entités parentes.
     */
    public ColisProduit(Colis colis, Produit produit, Integer quantite) {
        this.colis = colis;
        this.produit = produit;
        this.quantite = quantite;
        this.dateAjout = LocalDateTime.now();
    }
}