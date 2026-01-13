package com.vahor.Usuarios_Backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.vahor.Usuarios_Backend.entities.User;
import com.vahor.Usuarios_Backend.models.UserRequest;

public interface UserService extends UserDetailsService {

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    Optional<User> findById(@NonNull Long id);

    Optional<User> findByUsername(String username);

    User save(User user);

    Optional<User> update(UserRequest user, Long id);

    void deleteById(Long id);

    // Nuevo método para autenticar usuarios
    boolean authenticate(String username, String password);

    // Método para verificar si el usuario actual es el mismo que el del parámetro
    boolean isCurrentUser(Long id);
}
