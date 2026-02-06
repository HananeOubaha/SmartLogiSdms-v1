package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.model.Colis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ColisRepository extends JpaRepository<Colis, String>, JpaSpecificationExecutor<Colis> {

    // 👇 CORRECTION: Rje3na l'String bach t-tafeq m3a Entity dyalk
    List<Colis> findByClientExpediteur_Id(String clientExpediteurId);

    List<Colis> findByDestinataireId(String destinataireId);
    List<Colis> findByLivreurId(String livreurId);
}