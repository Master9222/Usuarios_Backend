package com.vahor.Usuarios_Backend.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.vahor.Usuarios_Backend.entities.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByName(String name);

}
