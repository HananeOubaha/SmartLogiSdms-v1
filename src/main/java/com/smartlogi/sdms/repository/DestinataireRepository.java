package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.model.Destinataire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Suppression de l'import java.util.UUID

@Repository
// CORRECTION CLÉ : Remplacer UUID par String
public interface DestinataireRepository extends JpaRepository<Destinataire, String> {

    // Pas de méthodes de recherche spécifiques requises pour l'instant.
}