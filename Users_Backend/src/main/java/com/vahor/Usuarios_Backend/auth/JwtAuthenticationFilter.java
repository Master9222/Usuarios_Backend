package com.vahor.users_back.auth;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vahor.users_back.entities.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.vahor.users_back.auth.TokenJwtConfig.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);

            if (user.getUsername() == null || user.getPassword() == null) {
                logger.warn("Credenciales faltantes: username o password no proporcionados");
                throw new AuthenticationException("Credenciales faltantes") {
                };
            }

            logger.info("Intentando autenticar usuario: {}", user.getUsername());
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        } catch (IOException e) {
            logger.error("Error leyendo la solicitud para autenticación", e);
            throw new AuthenticationException("Error procesando la solicitud de autenticación", e) {
            };
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authResult
                .getPrincipal();
        String username = user.getUsername();
        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();

        boolean isAdmin = roles.stream()
                .anyMatch(role -> role.getAuthority().equalsIgnoreCase("ADMIN") ||
                        role.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        claims.put("username", username);
        claims.put("isAdmin", isAdmin);

        logger.debug("Generando token JWT con claims: {}", claims);

        @SuppressWarnings("deprecation")
        String jwt = Jwts.builder()
                .setSubject(username)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();

        logger.info("Usuario autenticado exitosamente: {}", username);

        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + jwt);

        Map<String, String> body = new HashMap<>();
        body.put("token", PREFIX_TOKEN + jwt);
        body.put("username", username);
        body.put("message", String.format("Hola %s, has iniciado sesión con éxito", username));

        logger.debug("Token enviado al cliente: {}", body);

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setContentType(CONTENT_TYPE);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {

        logger.warn("Fallo de autenticación: {}", failed.getMessage());

        Map<String, String> body = new HashMap<>();
        body.put("message", "Error en la autenticación: username o password incorrecto!");
        body.put("error", failed.getMessage());

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setContentType(CONTENT_TYPE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
