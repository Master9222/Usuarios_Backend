package com.vahor.Usuarios_Backend.services;

import java.util.List;
import java.util.Optional;
import com.vahor.Usuarios_Backend.entities.User;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    void deleteById(Long id);

}
