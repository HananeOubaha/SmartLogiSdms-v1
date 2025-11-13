package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.model.Colis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Ajouté pour la pagination/filtres
import org.springframework.stereotype.Repository;

// Suppression de l'import java.util.UUID
import java.util.List;

@Repository
// CORRECTION CLÉ : Remplacer UUID par String, et ajouter JpaSpecificationExecutor
public interface ColisRepository extends JpaRepository<Colis, String>, JpaSpecificationExecutor<Colis> {

    /**
     * Future User Story: Je veux consulter la liste de mes colis en cours et livrés (Client expéditeur)
     * @param clientExpediteurId L'ID du client (String)
     */
    List<Colis> findByClientExpediteurId(String clientExpediteurId);

    /**
     * Future User Story: Je veux consulter le statut de colis qui me sont destinés (Destinataire)
     * @param destinataireId L'ID du destinataire (String)
     */
    List<Colis> findByDestinataireId(String destinataireId);

    /**
     * Future User Story: Je veux voir la liste de mes colis assignés (Livreur)
     * @param livreurId L'ID du livreur (String)
     */
    List<Colis> findByLivreurId(String livreurId);
}