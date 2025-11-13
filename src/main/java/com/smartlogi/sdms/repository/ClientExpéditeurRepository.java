package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.model.ClientExpéditeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
// Suppression de l'import java.util.UUID
import java.util.Optional;

@Repository
// CORRECTION CLÉ : Remplacer UUID par String
public interface ClientExpéditeurRepository extends JpaRepository<ClientExpéditeur, String> {

    /**
     * Recherche un client expéditeur par son email (utilisé pour la vérification d'unicité).
     * @param email L'email à rechercher.
     * @return ClientExpéditeur s'il existe.
     */
    Optional<ClientExpéditeur> findByEmail(String email);
}