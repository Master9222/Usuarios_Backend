package com.vahor.Usuarios_Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.vahor.Usuarios_Backend.entities.User;

@Repository

public interface UserRepository extends CrudRepository<User, Long> {
}
