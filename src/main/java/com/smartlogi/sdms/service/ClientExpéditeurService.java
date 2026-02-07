package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.ClientExpéditeurDto;
import com.smartlogi.sdms.mapper.ClientExpéditeurMapper;
import com.smartlogi.sdms.model.ClientExpéditeur;
import com.smartlogi.sdms.repository.ClientExpéditeurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import com.smartlogi.sdms.enums.RoleName;
import com.smartlogi.sdms.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientExpéditeurService {

    private final ClientExpéditeurRepository clientExpéditeurRepository;
    private final ClientExpéditeurMapper clientExpéditeurMapper;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // CREATE
    public ClientExpéditeurDto createClient(ClientExpéditeurDto clientDto) {

        // Logique métier: Vérification de l'unicité de l'email
        if (clientExpéditeurRepository.findByEmail(clientDto.getEmail()).isPresent()) {
            throw new DataIntegrityViolationException(
                    "L'email " + clientDto.getEmail() + " est déjà utilisé par un autre client.");
        }

        ClientExpéditeur client = clientExpéditeurMapper.toEntity(clientDto);

        // Encodage du mot de passe pour l'entité ClientExpéditeur
        if (clientDto.getPassword() != null) {
            client.setPassword(passwordEncoder.encode(clientDto.getPassword()));
            // Synchronisation avec la table USERS pour l'authentification
            userService.createOrUpdateUser(clientDto.getEmail(), clientDto.getPassword(), RoleName.ROLE_CLIENT);
        }

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
    public ClientExpéditeurDto getClientById(String id) {
        ClientExpéditeur client = clientExpéditeurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client expéditeur non trouvé avec l'ID: " + id));
        return clientExpéditeurMapper.toDto(client);
    }

    // FETCH ENTITY (pour usage interne, ex: dans ColisService)
    public ClientExpéditeur getClientEntityById(String id) {
        return clientExpéditeurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client expéditeur non trouvé avec l'ID: " + id));
    }

    // UPDATE
    public ClientExpéditeurDto updateClient(String id, ClientExpéditeurDto clientDto) {
        ClientExpéditeur existingClient = clientExpéditeurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client expéditeur non trouvé avec l'ID: " + id));

        // Mettre à jour les champs
        existingClient.setNom(clientDto.getNom());
        existingClient.setPrenom(clientDto.getPrenom());
        existingClient.setTelephone(clientDto.getTelephone());
        existingClient.setAdresse(clientDto.getAdresse());

        if (!existingClient.getEmail().equals(clientDto.getEmail()) &&
                clientExpéditeurRepository.findByEmail(clientDto.getEmail()).isPresent()) {
            throw new DataIntegrityViolationException(
                    "L'email " + clientDto.getEmail() + " est déjà utilisé par un autre client.");
        }
        existingClient.setEmail(clientDto.getEmail());

        // Mise à jour du mot de passe si fourni
        if (clientDto.getPassword() != null && !clientDto.getPassword().isEmpty()) {
            existingClient.setPassword(passwordEncoder.encode(clientDto.getPassword()));
            // Synchronisation avec la table USERS
            userService.createOrUpdateUser(clientDto.getEmail(), clientDto.getPassword(), RoleName.ROLE_CLIENT);
        }

        ClientExpéditeur updatedClient = clientExpéditeurRepository.save(existingClient);
        return clientExpéditeurMapper.toDto(updatedClient);
    }

    // DELETE
    public void deleteClient(String id) {
        if (!clientExpéditeurRepository.existsById(id)) {
            throw new EntityNotFoundException("Client expéditeur non trouvé avec l'ID: " + id);
        }
        clientExpéditeurRepository.deleteById(id);
    }

    public List<ClientExpéditeurDto> afficherClient(String adress) {
        List<ClientExpéditeur> clients = clientExpéditeurRepository.findAll();
        return clients.stream()
                .filter(client -> adress.equals(client.getAdresse()))
                .map(client -> clientExpéditeurMapper.toDto(client))
                .toList();
    }
}