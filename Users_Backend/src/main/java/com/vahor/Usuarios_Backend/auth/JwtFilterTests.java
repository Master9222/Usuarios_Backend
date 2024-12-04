package com.vahor.users_back.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtFilterTests {

    @Mock
    private JwtValidationFilter jwtValidationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateToken() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        // Simula un token en el encabezado de autorización
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");

        // Simula el comportamiento del filtro
        jwtValidationFilter.doFilterInternal(request, response, chain);

        // Verifica el resultado
        assertEquals(200, response.getStatus());
    }
}
