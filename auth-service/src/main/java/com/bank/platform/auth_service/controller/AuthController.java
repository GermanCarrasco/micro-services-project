package com.bank.platform.auth_service.controller;

import com.bank.platform.auth_service.dto.AuthRequest;
import com.bank.platform.auth_service.dto.AuthResponse;
import com.bank.platform.auth_service.dto.RefreshRequest;
import com.bank.platform.auth_service.dto.RegisterRequest;
import com.bank.platform.auth_service.entities.RefreshToken;
import com.bank.platform.auth_service.entities.User;
import com.bank.platform.auth_service.repository.IRefreshTokenRepository;
import com.bank.platform.auth_service.service.AuthService;
import com.bank.platform.auth_service.service.JwtService;
import com.bank.platform.auth_service.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest user){
        authService.register(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {

        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(@RequestBody RefreshRequest refreshRequest){

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshRequest.getRefreshToken())
                .orElseThrow();

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getUsername());

        String newAccessToken = jwtService.generateToken(userDetails);

        return new AuthResponse(newAccessToken,refreshRequest.getRefreshToken());
    }

}
