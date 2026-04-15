package com.bank.platform.api_gateway.filter;


import com.bank.platform.security.JwtValidatorService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secret;

    private JwtValidatorService jwtValidatorService;

    @PostConstruct
    public void init(){
        this.jwtValidatorService = new JwtValidatorService(secret);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // permitir login y refresh
        if (path.startsWith("/auth")) {
            return chain.filter(exchange);
        }

        String authHeader =
                exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtValidatorService.extractAllClaims(token);

            //Extraer data
            String userName = claims.getSubject();
            String userId = claims.get("userId").toString();
            String role = claims.get("role").toString();

            //Agregar Headers a la request
            ServerHttpRequest mutateRequest = exchange.getRequest().mutate()
                    .header("X-User-Username",userName)
                    .header("X-User-Role",role)
                    .header("X-User-Id",userId).build();

            return chain.filter(exchange.mutate().request(mutateRequest).build());

        } catch (Exception e) {

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

    }

    @Override
    public int getOrder() {
        return -1;
    }
}
