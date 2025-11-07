package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.model.Destinataire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface DestinataireRepository extends JpaRepository<Destinataire, UUID> {

}