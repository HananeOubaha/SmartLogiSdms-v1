package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.model.Livreur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
// Suppression de l'import java.util.UUID

@Repository
// CORRECTION CLÉ : Remplacer UUID par String
public interface LivreurRepository extends JpaRepository<Livreur, String> {

    // Pour les futures fonctionnalités, le JpaRepository nous permet déjà de gérer la pagination.
}