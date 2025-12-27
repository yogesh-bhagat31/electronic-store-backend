package com.learn.electronicstore.services;

import com.learn.electronicstore.dtos.GoogleLoginRequest;
import com.learn.electronicstore.dtos.JwtRequest;
import com.learn.electronicstore.dtos.JwtResponse;
import com.learn.electronicstore.dtos.RefreshTokenRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface AuthenticationService {

    JwtResponse authenticate(JwtRequest jwtRequest);

    ResponseEntity<JwtResponse> loginWithGoogle(@RequestBody GoogleLoginRequest googleLoginRequest) throws GeneralSecurityException, IOException;

    public ResponseEntity<JwtResponse> regenerateToken(@RequestBody RefreshTokenRequest request);
}
