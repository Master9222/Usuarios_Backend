package com.vahor.Usuarios_Backend.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SpringSecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // Deshabilitar CSRF para API REST
                .csrf(AbstractHttpConfigurer::disable)
                // Configuración de autorización
                .authorizeHttpRequests(authz -> authz
                        // Endpoints públicos
                        .requestMatchers("/api/auth/**", "/login", "/resources/**", "/css/**", "/js/**", "/images/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Endpoints para usuarios autenticados
                        .requestMatchers("/api/dashboard", "/dashboard").authenticated()
                        // Endpoints para administradores
                        .requestMatchers("/api/users/**", "/users/**").hasRole("ADMIN")
                        // Páginas de error
                        .requestMatchers("/403", "/access-denied").permitAll()
                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated())
                // Configuración de sesión sin estado (stateless) para JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Añadir filtro JWT
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Manejo de excepciones
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(new AccessDeniedHandlerImpl())
                        .accessDeniedPage("/403"))
                .build();
    }
}
