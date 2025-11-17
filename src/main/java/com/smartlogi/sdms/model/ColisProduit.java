package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; // Ajouté pour cohérence
import java.time.LocalDateTime;
// Suppression de l'import java.util.UUID

@Entity
@Table(name = "colis_produit")
@Data
@NoArgsConstructor
@AllArgsConstructor // Ajouté pour cohérence
public class ColisProduit {

    // Utilisation de la clé primaire composée
    @EmbeddedId
    private ColisProduitId id;

    // Mappe l'ID du Colis
    // Note: Le type de 'colis' est maintenant basé sur String ID
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("colisId")
    @JoinColumn(name = "colis_id")
    private Colis colis;

    // Mappe l'ID du Produit
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("produitId")
    @JoinColumn(name = "produit_id")
    private Produit produit;

    private Integer quantite;

    @Column(name = "prix_unitaire")
    private Double prixUnitaire;

    @Column(name = "date_ajout")
    private LocalDateTime dateAjout;

    /**
     * Constructeur utilitaire pour la création.
     * Initialise la clé composée avec les IDs en String des entités parentes.
     */
    public ColisProduit(Colis colis, Produit produit, Integer quantite) {
        // CORRECTION : L'ID composé doit être créé en utilisant les IDs String
        // Assurez-vous que ColisProduitId accepte String pour ses paramètres.
        this.id = new ColisProduitId(colis.getId(), produit.getId());

        this.colis = colis;
        this.produit = produit;
        this.quantite = quantite;
        this.dateAjout = LocalDateTime.now();
    }
}