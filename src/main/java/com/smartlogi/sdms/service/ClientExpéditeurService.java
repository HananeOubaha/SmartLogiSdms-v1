package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.ClientExpéditeurDto;
import com.smartlogi.sdms.mapper.ClientExpéditeurMapper;
import com.smartlogi.sdms.model.ClientExpéditeur;
import com.smartlogi.sdms.repository.ClientExpéditeurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.UUID;

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
    public ClientExpéditeurDto getClientById(UUID id){
        ClientExpéditeur client = clientExpéditeurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client expéditeur non trouvé avec l'ID: " + id));
        return clientExpéditeurMapper.toDto(client);
    }

    // FETCH ENTITY (pour usage interne, ex: dans ColisService)
    public ClientExpéditeur getClientEntityById(UUID id) {
        return clientExpéditeurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client expéditeur non trouvé avec l'ID: " + id));
    }


    // UPDATE
    public ClientExpéditeurDto updateClient(UUID id, ClientExpéditeurDto clientDto) {
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
    public void deleteClient(UUID id) {
        if (!clientExpéditeurRepository.existsById(id)) {
            throw new EntityNotFoundException("Client expéditeur non trouvé avec l'ID: " + id);
        }
        clientExpéditeurRepository.deleteById(id);
    }
}