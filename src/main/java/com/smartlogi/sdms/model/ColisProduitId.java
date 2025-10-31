package com.smartlogi.sdms.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor // Requis par JPA
@AllArgsConstructor // Simplifie la création
public class ColisProduitId implements Serializable {

    // Correspond à l'ID de l'entité Colis
    private Long colisId;

    // Correspond à l'ID de l'entité Produit
    private Long produitId;

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