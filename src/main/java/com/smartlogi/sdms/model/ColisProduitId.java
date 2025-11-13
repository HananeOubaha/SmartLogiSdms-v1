package com.smartlogi.sdms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.util.Objects;
// Suppression de l'import java.util.UUID

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColisProduitId implements Serializable {

    // Correspond à l'ID de l'entité Colis (FK)
    @Column(name = "colis_id", columnDefinition = "VARCHAR(36)")
    private String colisId; // <-- CORRECTION: Changé de UUID à String

    // Correspond à l'ID de l'entité Produit (FK)
    @Column(name = "produit_id", columnDefinition = "VARCHAR(36)")
    private String produitId; // <-- CORRECTION: Changé de UUID à String

    // IMPORTANT : Redéfinir equals et hashCode pour la clé composée
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
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