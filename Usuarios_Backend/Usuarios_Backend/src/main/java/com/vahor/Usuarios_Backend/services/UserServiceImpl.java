package com.vahor.Usuarios_Backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.vahor.Usuarios_Backend.entities.User;
import com.vahor.Usuarios_Backend.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List) this.repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(@NonNull Long id) {
        return repository.findById(id);
    };

    @Override
    @Transactional
    public User save(User user) {
        return repository.save(user);
    };

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    };
}
