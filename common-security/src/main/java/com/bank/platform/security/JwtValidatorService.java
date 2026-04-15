
package com.bank.platform.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

public class JwtValidatorService {

    private final String secret;

    public JwtValidatorService(String secret) {
        this.secret = secret;
    }

    // Extraer username
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Método genérico
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    // Extraer todos los claims
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Expiración
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Validar expiración
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validación simple (sin DB)
    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role").toString();
    }

    public Long extractUserId(String token) {
        Object userId = extractAllClaims(token).get("userId");
        return userId != null ? Long.parseLong(userId.toString()) : null;
    }
}