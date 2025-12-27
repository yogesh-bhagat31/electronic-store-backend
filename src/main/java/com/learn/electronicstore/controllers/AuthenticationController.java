package com.learn.electronicstore.controllers;

import com.learn.electronicstore.dtos.*;
import com.learn.electronicstore.services.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/generate-token")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest jwtRequest) {
        log.info("Username: " + jwtRequest.getEmail() + " Password: " + jwtRequest.getPassword() + "");
        JwtResponse response = authenticationService.authenticate(jwtRequest);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/google-login")
    public ResponseEntity<JwtResponse> loginWithGoogle(@RequestBody GoogleLoginRequest googleLoginRequest) throws GeneralSecurityException, IOException {
        return authenticationService.loginWithGoogle(googleLoginRequest);
    }

    @PostMapping("/regenerate-token")
    public ResponseEntity<JwtResponse> regenerateToken(@RequestBody RefreshTokenRequest request) {

        return authenticationService.regenerateToken(request);

    }

}
