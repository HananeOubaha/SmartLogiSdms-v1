package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.model.Livreur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface LivreurRepository extends JpaRepository<Livreur, UUID> {

    // Pour les futures fonctionnalités, le JpaRepository nous permet déjà de gérer la pagination.
}