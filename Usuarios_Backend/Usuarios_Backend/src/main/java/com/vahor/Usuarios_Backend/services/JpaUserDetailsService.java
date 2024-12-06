package com.vahor.Usuarios_Backend.services;

import java.util.List;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vahor.Usuarios_Backend.entities.User;
import com.vahor.Usuarios_Backend.repositories.UserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(JpaUserDetailsService.class);
    private static final String ROLE_PREFIX = "ROLE_";

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Log de intento de inicio de sesión
        logger.info("Intento de inicio de sesión para el usuario: '{}'", username);

        // Buscar el usuario en la base de datos
        User user = repository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Error en el login: no existe el usuario '{}'", username);
                    return new UsernameNotFoundException(
                            String.format("El usuario '%s' no existe en el sistema", username));
                });

        // Validar si el usuario está habilitado
        if (!user.isEnabled()) {
            logger.error("Error en el login: el usuario '{}' está deshabilitado", username);
            throw new UsernameNotFoundException(String.format("El usuario '%s' está deshabilitado", username));
        }

        // Validar si la cuenta está bloqueada
        if (user.isAccountLocked()) {
            logger.error("Error en el login: la cuenta del usuario '{}' está bloqueada", username);
            throw new UsernameNotFoundException(String.format("La cuenta del usuario '%s' está bloqueada", username));
        }

        // Obtener roles del usuario y convertirlos en GrantedAuthority
        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> {
                    String roleName = role.getName();
                    if (!roleName.startsWith(ROLE_PREFIX)) {
                        roleName = ROLE_PREFIX + roleName;
                    }
                    return new SimpleGrantedAuthority(roleName);
                })
                .collect(Collectors.toList());

        // Validar si el usuario tiene roles asignados
        if (authorities.isEmpty()) {
            logger.error("Error en el login: el usuario '{}' no tiene roles asignados", username);
            throw new UsernameNotFoundException(
                    String.format("El usuario '%s' no tiene roles asignados", username));
        }

        // Log del usuario encontrado
        logger.info("Inicio de sesión exitoso para el usuario '{}' con roles: {}", username, authorities);

        // Retornar un UserDetails con propiedades dinámicas
        return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                user.isEnabled(), // Activar o desactivar el usuario según su estado
                true, // La cuenta no está expirada
                true, // Las credenciales no están expiradas
                !user.isAccountLocked(), // La cuenta no está bloqueada
                authorities);
    }
}
