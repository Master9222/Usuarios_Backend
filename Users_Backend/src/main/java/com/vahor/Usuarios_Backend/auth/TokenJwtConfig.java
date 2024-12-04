package com.vahor.users_back.auth;

import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;

public class TokenJwtConfig {

    // Tipo de contenido para las respuestas
    public static final String CONTENT_TYPE = "application/json";

    // Prefijo para el token en la cabecera Authorization
    public static final String PREFIX_TOKEN = "Bearer ";

    // Nombre de la cabecera HTTP para el token
    public static final String HEADER_AUTHORIZATION = "Authorization";

    // Clave secreta para firmar y validar los tokens (mínimo 32 caracteres)
    private static final String SECRET_KEY_STRING = "uHn3pNq7xR2d5Tg8ZkLp9Qs7bVrWyAx7CdPfGhVjKtMqPxYz";

    // Convierte la clave secreta en un objeto SecretKey
    public static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));

    // Tiempo de expiración del token en milisegundos (1 hora)
    public static final long EXPIRATION_TIME = 3600000;

    // Constructor privado para evitar instancias
    private TokenJwtConfig() {
        throw new UnsupportedOperationException(
                "Esta clase es un contenedor de configuración y no debe ser instanciada");
    }
}
