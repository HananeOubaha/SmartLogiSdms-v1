package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.ClientExpéditeurDto;
import com.smartlogi.sdms.mapper.ClientExpéditeurMapper;
import com.smartlogi.sdms.model.ClientExpéditeur;
import com.smartlogi.sdms.repository.ClientExpéditeurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
// Suppression de l'import java.util.UUID car il n'est plus utilisé comme argument
// Note: Il n'est pas utilisé non plus pour la génération d'ID ici, car le code
// utilise this.id = java.util.UUID.randomUUID().toString() dans l'Entité via @PrePersist.

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientExpéditeurService {

    private final ClientExpéditeurRepository clientExpéditeurRepository;
    private final ClientExpéditeurMapper clientExpéditeurMapper;

    // CREATE
    public ClientExpéditeurDto createClient(ClientExpéditeurDto clientDto) {

        // Logique métier: Vérification de l'unicité de l'email
        if (clientExpéditeurRepository.findByEmail(clientDto.getEmail()).isPresent()) {
            throw new DataIntegrityViolationException("L'email " + clientDto.getEmail() + " est déjà utilisé par un autre client.");
        }

        ClientExpéditeur client = clientExpéditeurMapper.toEntity(clientDto);
        // L'ID String est généré par @PrePersist dans l'Entité
        ClientExpéditeur savedClient = clientExpéditeurRepository.save(client);
        return clientExpéditeurMapper.toDto(savedClient);
    }

    // READ ALL
    public List<ClientExpéditeurDto> getAllClients() {
        return clientExpéditeurRepository.findAll().stream()
                .map(clientExpéditeurMapper::toDto)
                .collect(Collectors.toList());
    }

    // READ BY ID
    // CORRECTION : Le paramètre id doit être String
    public ClientExpéditeurDto getClientById(String id){
        ClientExpéditeur client = clientExpéditeurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client expéditeur non trouvé avec l'ID: " + id));
        return clientExpéditeurMapper.toDto(client);
    }

    // FETCH ENTITY (pour usage interne, ex: dans ColisService)
    // CORRECTION : Le paramètre id doit être String
    public ClientExpéditeur getClientEntityById(String id) {
        return clientExpéditeurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client expéditeur non trouvé avec l'ID: " + id));
    }


    // UPDATE
    // CORRECTION : Le paramètre id doit être String
    public ClientExpéditeurDto updateClient(String id, ClientExpéditeurDto clientDto) {
        // L'appel findById(id) est maintenant correct car id est un String
        ClientExpéditeur existingClient = clientExpéditeurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client expéditeur non trouvé avec l'ID: " + id));

        // Mettre à jour les champs (l'ID n'est pas modifié)
        existingClient.setNom(clientDto.getNom());
        existingClient.setPrenom(clientDto.getPrenom());
        existingClient.setTelephone(clientDto.getTelephone());
        existingClient.setAdresse(clientDto.getAdresse());

        // Note: L'email ne doit pas être mis à jour s'il existe déjà ailleurs
        if (!existingClient.getEmail().equals(clientDto.getEmail()) &&
                clientExpéditeurRepository.findByEmail(clientDto.getEmail()).isPresent()) {
            throw new DataIntegrityViolationException("L'email " + clientDto.getEmail() + " est déjà utilisé par un autre client.");
        }
        existingClient.setEmail(clientDto.getEmail());

        ClientExpéditeur updatedClient = clientExpéditeurRepository.save(existingClient);
        return clientExpéditeurMapper.toDto(updatedClient);
    }

    // DELETE
    // CORRECTION : Le paramètre id doit être String
    public void deleteClient(String id) {
        // L'appel existsById(id) et deleteById(id) sont maintenant corrects
        if (!clientExpéditeurRepository.existsById(id)) {
            throw new EntityNotFoundException("Client expéditeur non trouvé avec l'ID: " + id);
        }
        clientExpéditeurRepository.deleteById(id);
    }

    // NOUVELLE MÉTHODE (Pour le moment, nous laissons la logique de filtrage inefficace pour ne pas modifier le code métier existant,
    // mais elle sera revue lors de la tâche de filtration avancée.)
    public List<ClientExpéditeurDto> afficherClient(String adress){
        List<ClientExpéditeur> clients = clientExpéditeurRepository.findAll();
        return clients.stream()
                .filter(client -> adress.equals(client.getAdresse()))
                .map(client -> clientExpéditeurMapper.toDto(client))
                .toList();
    }
}