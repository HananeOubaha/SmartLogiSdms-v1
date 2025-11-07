package com.smartlogi.sdms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID; // <-- Import nécessaire

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColisProduitId implements Serializable {

    // Correspond à l'ID de l'entité Colis (FK)
    @Column(name = "colis_id", columnDefinition = "VARCHAR(36)") // Assure le mapping DB
    private UUID colisId; // <-- CORRECTION: Changé de Long à UUID

    // Correspond à l'ID de l'entité Produit (FK)
    @Column(name = "produit_id", columnDefinition = "VARCHAR(36)") // Assure le mapping DB
    private UUID produitId; // <-- CORRECTION: Changé de Long à UUID

    // IMPORTANT : Redéfinir equals et hashCode pour la clé composée
    // (Les implémentations générées par Lombok ou fournies ici restent valides,
    // car elles utilisent la méthode equals/hashcode de l'objet UUID.)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // La vérification de la classe doit être mise à jour si vous avez renommé le package, mais elle est généralement correcte.
        if (o == null || getClass() != o.getClass()) return false;
        ColisProduitId that = (ColisProduitId) o;
        return Objects.equals(colisId, that.colisId) &&
                Objects.equals(produitId, that.produitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(colisId, produitId);
    }
}