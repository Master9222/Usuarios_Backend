package com.vahor.users_back.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.vahor.users_back.entities.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByName(String name);

}
