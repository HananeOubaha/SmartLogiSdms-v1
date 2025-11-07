package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.model.Colis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface ColisRepository extends JpaRepository<Colis, UUID> {

    // Future User Story: Je veux consulter la liste de mes colis en cours et livrés (Client expéditeur)
    List<Colis> findByClientExpediteurId(UUID clientExpediteurId);

    // Future User Story: Je veux consulter le statut de colis qui me sont destinés (Destinataire)
    List<Colis> findByDestinataireId(UUID destinataireId);

    // Future User Story: Je veux voir la liste de mes colis assignés (Livreur)
    List<Colis> findByLivreurId(UUID livreurId);
}