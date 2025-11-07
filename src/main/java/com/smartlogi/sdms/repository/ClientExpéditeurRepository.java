package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.model.ClientExpéditeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface ClientExpéditeurRepository extends JpaRepository<ClientExpéditeur, UUID> {

    // Ajout d'une méthode pour vérifier l'unicité par email, très utile.
    Optional<ClientExpéditeur> findByEmail(String email);
}