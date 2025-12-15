package com.smartlogi.sdms.security.core;

import com.smartlogi.sdms.model.ClientExpéditeur;
import com.smartlogi.sdms.repository.ClientExpéditeurRepository;
import com.smartlogi.sdms.repository.LivreurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ClientExpéditeurRepository clientRepository;
    private final LivreurRepository livreurRepository;

    /**
     * Charge l'utilisateur par son username (ici, l'email).
     * Cette méthode est appelée automatiquement par Spring Security lors du login.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Chercher dans la table Client Expéditeur
        Optional<ClientExpéditeur> client = clientRepository.findByEmail(email);
        if (client.isPresent()) {
            ClientExpéditeur c = client.get();
            // Retourne notre adaptateur SecurityUser
            return new SecurityUser(
                    c.getId(),
                    c.getEmail(),
                    c.getPassword(),
                    c.getRole()
            );
        }

        // 2. Si non trouvé, chercher dans la table Livreur
        // Note: Il faudra ajouter une méthode findByTelephone ou findByNom dans LivreurRepository
        // Pour l'instant, supposons que le livreur a aussi un champ email ou identifiant unique
        // Si Livreur n'a pas d'email unique, il faudra adapter (ex: utiliser le téléphone comme username)

        // (Simulation pour Livreur en attendant l'ajout de l'email ou username unique)
        // Optional<Livreur> livreur = livreurRepository.findByTelephone(email);

        throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email);
    }
}