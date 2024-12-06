package com.vahor.Usuarios_Backend.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

@Configuration
public class SpringSecurityConfig {

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
                // Configuración de autorización
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/login", "/resources/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/dashboard").authenticated()
                        .requestMatchers("/users/**").hasRole("ADMIN") // Spring añade automáticamente el prefijo
                                                                       // "ROLE_"
                        .requestMatchers("/403").permitAll()
                        .anyRequest().authenticated())
                // Configuración del formulario de inicio de sesión
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error")
                        .permitAll())
                // Configuración de cierre de sesión
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                // Manejo de excepciones
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(new AccessDeniedHandlerImpl())
                        .accessDeniedPage("/403"))
                // Deshabilitar CSRF en desarrollo
                .csrf(csrf -> csrf.disable())
                .build();
    }
}
