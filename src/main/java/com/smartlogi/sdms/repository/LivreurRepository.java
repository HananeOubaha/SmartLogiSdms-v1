package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.model.Livreur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LivreurRepository extends JpaRepository<Livreur, String> {

    // 🔥 Recherche d'un livreur par email
    Optional<Livreur> findByEmail(String email);
}
