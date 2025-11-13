package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
// Suppression de l'import java.util.UUID

/**
 * Repository pour l'entité Zone.
 * Fournit les opérations CRUD (Create, Read, Update, Delete) via Spring Data JPA.
 */
@Repository // Cette annotation permet à Spring de trouver et d'injecter ce bean
// CORRECTION CLÉ : Remplacer UUID par String
public interface ZoneRepository extends JpaRepository<Zone, String> {

    // JpaRepository hérite automatiquement des méthodes suivantes:
    // save(), findAll(), findById(), existsById(), deleteById(), etc.

    // Vous pouvez ajouter des méthodes de recherche personnalisées ici, si nécessaire.
    // Exemple : Optional<Zone> findByNom(String nom);
}