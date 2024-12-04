package com.vahor.users_back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(configurationSource());
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Orígenes permitidos (usar restricciones en producción)
        config.addAllowedOriginPattern("*"); // Cambiar "*" por dominios específicos en producción
        config.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE")); // Métodos permitidos
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // Encabezados permitidos
        config.setAllowCredentials(true); // Permitir credenciales

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
