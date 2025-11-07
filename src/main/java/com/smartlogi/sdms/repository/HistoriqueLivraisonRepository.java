package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.model.HistoriqueLivraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface HistoriqueLivraisonRepository extends JpaRepository<HistoriqueLivraison, UUID> {

    // Méthode pour obtenir tout l'historique d'un colis spécifique
    List<HistoriqueLivraison> findByColisIdOrderByDateChangementDesc(UUID colisId);
}