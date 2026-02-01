package com.smartlogi.sdms.service;

import com.smartlogi.sdms.enums.RoleName;
import com.smartlogi.sdms.model.Role;
import com.smartlogi.sdms.model.User;
import com.smartlogi.sdms.repository.RoleRepository;
import com.smartlogi.sdms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void createOrUpdateUser(String username, String rawPassword, RoleName roleName) {
        User user = userRepository.findByUsername(username).orElse(new User());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role non trouvé: " + roleName));

        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        userRepository.save(user);
    }
}
