package com.vahor.Usuarios_Backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vahor.Usuarios_Backend.entities.Role;
import com.vahor.Usuarios_Backend.entities.User;
import com.vahor.Usuarios_Backend.models.IUser;
import com.vahor.Usuarios_Backend.models.UserRequest;
import com.vahor.Usuarios_Backend.repositories.RoleRepository;
import com.vahor.Usuarios_Backend.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository repository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List) this.repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(@NonNull Long id) {
        Optional<User> userOptional = repository.findById(id);
        userOptional.ifPresent(user -> user.getRoles().size()); // Inicializa los roles
        return userOptional;
    }

    @Transactional
    @Override
    public User save(User user) {
        user.setRoles(generateUniqueRoles(user));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @Override
    @Transactional
    public Optional<User> update(UserRequest user, Long id) {
        Optional<User> userOptional = repository.findById(id);

        if (userOptional.isPresent()) {
            User userDb = userOptional.get();
            userDb.setEmail(user.getEmail());
            userDb.setLastname(user.getLastname());
            userDb.setName(user.getName());
            userDb.setUsername(user.getUsername());

            // Actualización de la contraseña solo si se proporciona
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                userDb.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            userDb.setRoles(generateUniqueRoles(user));
            return Optional.of(repository.save(userDb));
        }
        return Optional.empty();
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        // Buscar al usuario por el username
        Optional<User> userOptional = repository.findByUsername(username);

        // Validar la existencia del usuario y comparar contraseñas
        return userOptional.map(user -> passwordEncoder.matches(password, user.getPassword())).orElse(false);
    }

    private List<Role> generateUniqueRoles(IUser user) {
        List<Role> roles = new ArrayList<>();

        // Generar rol único para "ROLE_USER"
        Role userRole = createUniqueRole("ROLE_USER");
        roles.add(userRole);

        // Generar rol único para "ROLE_ADMIN" si es admin
        if (user.isAdmin()) {
            Role adminRole = createUniqueRole("ROLE_ADMIN");
            roles.add(adminRole);
        }

        return roles;
    }

    private Role createUniqueRole(String baseRoleName) {
        List<Role> existingRoles = roleRepository.findByNameStartingWith(baseRoleName);

        // Generar un rol único basado en el ID más alto
        long suffix = existingRoles.stream()
                .mapToLong(role -> {
                    String[] parts = role.getName().split("_");
                    try {
                        return Long.parseLong(parts[parts.length - 1]);
                    } catch (NumberFormatException e) {
                        return 0L; // Si no tiene sufijo, lo considera 0
                    }
                })
                .max()
                .orElse(0L) + 1;

        String uniqueRoleName = baseRoleName + "_" + suffix;

        // Crear y guardar el rol único
        Role newRole = new Role(uniqueRoleName);
        return roleRepository.save(newRole);
    }
}
