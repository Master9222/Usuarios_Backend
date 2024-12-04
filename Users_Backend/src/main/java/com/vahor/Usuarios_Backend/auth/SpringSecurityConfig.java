package com.vahor.users_back.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Permitir
                                                                                                                    // acceso
                                                                                                                    // a
                                                                                                                    // /login
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyAuthority("ADMIN", "USER") // Rutas
                                                                                                           // protegidas
                        .requestMatchers(HttpMethod.POST, "/api/users/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated() // Requerir autenticación para el resto
                )
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para pruebas
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless
                .build();
    }
}
