package com.vahor.users_back.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.vahor.users_back.auth.TokenJwtConfig.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtValidationFilter extends BasicAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtValidationFilter.class);
    private final JwtParser jwtParser;

    @SuppressWarnings("deprecation")
    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        // Configurar el parser con la clave secreta directamente
        this.jwtParser = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader(HEADER_AUTHORIZATION);

        logger.debug("Cabecera Authorization recibida: {}", header);

        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            logger.warn("No se encontró el token o tiene un formato inválido");
            chain.doFilter(request, response);
            return;
        }

        String token = header.replace(PREFIX_TOKEN, "").trim();
        logger.debug("Token extraído: {}", token);

        try {
            // Validar y analizar el token
            @SuppressWarnings("deprecation")
            Claims claims = jwtParser.parseClaimsJws(token).getBody();

            logger.debug("Claims obtenidos del token: {}", claims);

            String username = claims.getSubject();
            Object authoritiesClaims = claims.get("roles");

            logger.debug("Datos extraídos: username={}, roles={}", username, authoritiesClaims);

            if (username == null || authoritiesClaims == null) {
                logger.error("El token no contiene información válida: username o roles faltantes");
                throw new JwtException("El token no contiene información válida");
            }

            // Convertir roles y configurar contexto de seguridad
            @SuppressWarnings("unchecked")
            Collection<? extends GrantedAuthority> roles = ((Collection<String>) authoritiesClaims).stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    username, null, roles);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            logger.info("Token validado exitosamente para el usuario: {}", username);

            chain.doFilter(request, response);

        } catch (JwtException e) {
            logger.error("Error durante la validación del token: {}", e.getMessage());
            enviarError(response, e.getMessage());
        }
    }

    private void enviarError(HttpServletResponse response, String mensajeError) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(CONTENT_TYPE);

        Map<String, String> body = new HashMap<>();
        body.put("message", "El token es inválido!");
        body.put("error", mensajeError);

        logger.debug("Enviando error al cliente: {}", body);
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
    }
}
