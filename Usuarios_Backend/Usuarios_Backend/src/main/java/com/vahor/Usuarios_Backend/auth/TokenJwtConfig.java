package com.vahor.Usuarios_Backend.auth;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class TokenJwtConfig {

    // Tipo de contenido que se devolverá
    public static final String CONTENT_TYPE = "application/json";

    // Prefijo del token en el encabezado de autorización
    public static final String PREFIX_TOKEN = "Bearer ";

    // Nombre del encabezado HTTP para autorización
    public static final String HEADER_AUTHORIZATION = "Authorization";

    // Clave secreta como texto codificado en Base64
    public static final String SECRET_KEY_STRING = Base64.getEncoder().encodeToString("your-secret-key".getBytes());

    // Método para obtener la clave secreta como objeto SecretKey
    public static SecretKey getSecretKey() {
        return new SecretKeySpec(SECRET_KEY_STRING.getBytes(), "HmacSHA256");
    }
}
