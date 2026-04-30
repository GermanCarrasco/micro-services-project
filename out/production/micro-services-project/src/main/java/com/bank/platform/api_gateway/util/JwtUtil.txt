package com.bank.platform.api_gateway.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    //Obtener los claims
    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token).getBody();
    }

    //Validar token
    public Claims validateToken(String token){
        return extractAllClaims(token);
    }

    //Extraer username
    public String extractUsername(String token){
        return extractAllClaims(token).getSubject();
    }

    //Extraer userId
    public Long extractUserId(String token){
        return extractAllClaims(token).get("userId", Long.class);
    }

    //Extraer role
    public String extractRole(String token){
        return extractAllClaims(token).get("role", String.class);
    }

}
