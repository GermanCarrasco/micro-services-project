package com.bank.platform.auth_service.service;


import com.bank.platform.security.JwtValidatorService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtGeneratorService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private JwtValidatorService jwtValidatorService;

    @PostConstruct
    public void init(){
        this.jwtValidatorService = new JwtValidatorService(secret);
    }

    // Generar token
    public String generateToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // Extraer username
    public String extractUsername(String token) {
        return jwtValidatorService.extractClaim(token, Claims::getSubject);
    }

    // Validar token
    public boolean isTokenValid(String token, UserDetails userDetails) {

        final String username = extractUsername(token);

        return username.equals(userDetails.getUsername()) && !jwtValidatorService.isTokenExpired(token);
    }
}



//
//    // Método genérico para extraer cualquier claim
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    // Extraer todos los claims
//    private Claims extractAllClaims(String token) {
//
//        return Jwts.parser()
//                .setSigningKey(secret)
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    // Extraer expiración
//    public Date extractExpiration(String token) {
//
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    // Verificar expiración
//    private boolean isTokenExpired(String token) {
//
//        return extractExpiration(token).before(new Date());
//    }
//