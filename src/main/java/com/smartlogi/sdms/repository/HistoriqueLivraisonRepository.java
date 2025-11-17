package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.model.HistoriqueLivraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Suppression de l'import java.util.UUID
import java.util.List;

@Repository
// CORRECTION CLÉ : Remplacer UUID par String pour la PK
public interface HistoriqueLivraisonRepository extends JpaRepository<HistoriqueLivraison, String> {

    /**
     * Méthode pour obtenir tout l'historique d'un colis spécifique
     * @param colisId L'ID du colis (String)
     */
    // CORRECTION : Le paramètre colisId doit être String
    List<HistoriqueLivraison> findByColisIdOrderByDateChangementDesc(String colisId);
}