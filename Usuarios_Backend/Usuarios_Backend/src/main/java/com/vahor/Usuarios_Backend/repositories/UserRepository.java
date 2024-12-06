package com.vahor.Usuarios_Backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vahor.Usuarios_Backend.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    // Nuevo método para buscar por email
    Optional<User> findByEmail(String email);
}
