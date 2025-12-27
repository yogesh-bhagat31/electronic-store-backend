package com.learn.electronicstore.services.servicesimpl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.learn.electronicstore.dtos.*;
import com.learn.electronicstore.entities.User;
import com.learn.electronicstore.enums.Providers;
import com.learn.electronicstore.exceptions.BadApiRequestException;
import com.learn.electronicstore.exceptions.ResourceNotFoundException;
import com.learn.electronicstore.security.JwtHelper;
import com.learn.electronicstore.services.AuthenticationService;
import com.learn.electronicstore.services.RefreshTokenService;
import com.learn.electronicstore.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private AuthenticationManager authenticationManager;

    private JwtHelper jwtHelper;
    private UserDetailsService userDetailsService;
    private ModelMapper modelMapper;
    private RefreshTokenService refreshTokenService;
    private AuthenticationService authenticationService;
    private UserService userService;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, UserService userService, AuthenticationService authenticationService, RefreshTokenService refreshTokenService, ModelMapper modelMapper, UserDetailsService userDetailsService, JwtHelper jwtHelper) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
        this.modelMapper = modelMapper;
        this.userDetailsService = userDetailsService;
        this.jwtHelper = jwtHelper;
    }

    @Value("${app.google.client_id}")
    private String googleClientId;

    @Value("${app.default_password}")
    private String googleProviderDefaultPassword;

    @Override
    public JwtResponse authenticate(JwtRequest jwtRequest) {
        try {
            Authentication authenticate = this.doAuthenticate(jwtRequest.getEmail(), jwtRequest.getPassword());
            UserDetails user = userDetailsService.loadUserByUsername(jwtRequest.getEmail());

            RefreshTokenDto refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

            String token = jwtHelper.generateToken(user);
            JwtResponse jwtResponse = JwtResponse
                    .builder()
                    .token(token)
                    .user(modelMapper.map(user, UserDto.class))
                    .refreshToken(refreshToken)
                    .build();
            return jwtResponse;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Invalid username/password supplied ");
        }

    }


    private Authentication doAuthenticate(String email, String password) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            log.info("Authenticated user: {}", authenticate.getName());
            log.info("Authenticated user: {}", authenticate.getAuthorities());
            log.info("User is authenticated: {}", authenticate.isAuthenticated());
            return authenticate;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Invalid username/password supplied ");
        }

    }


    @Override
    public ResponseEntity<JwtResponse> loginWithGoogle(GoogleLoginRequest googleLoginRequest) throws GeneralSecurityException, IOException {
        log.info(googleLoginRequest.getIdToken());
        // Token verify
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new ApacheHttpTransport(), new GsonFactory()).setAudience(List.of(googleClientId)).build();
        GoogleIdToken googleIdToken = verifier.verify(googleLoginRequest.getIdToken());
        if (googleIdToken != null) {
            //token is verified
            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();
            String userName = payload.getSubject();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            log.info(name);
            log.info(email);
            log.info(pictureUrl);
            log.info(userName);

            UserDto userDto = new UserDto();
            userDto.setName(name);
            userDto.setEmail(email);
            userDto.setImageName(pictureUrl);
            userDto.setPassword(googleProviderDefaultPassword);
            userDto.setAbout("user is created using google provider");
            userDto.setProvider(Providers.GOOGLE);

            //create user
            UserDto user = null;
            try {
                log.info("user loading from database");
                user = userService.getUserByEmail(userDto.getEmail());

                if (user.getProvider().equals(userDto.getProvider())) {

                } else {
                    throw new BadCredentialsException("User already registered with different provider !! Try to login with registered email and password.");
                }
            } catch (ResourceNotFoundException e) {
                log.info("user not found in database hence creating new user");
                user = userService.createUser(userDto);
            }

            //generate jwt token
            JwtRequest jwtRequest = new JwtRequest();
            jwtRequest.setEmail(user.getEmail());
            jwtRequest.setPassword(userDto.getPassword());

            // we will get token from jwtResponse
            JwtResponse response = authenticationService.authenticate(jwtRequest);

            return ResponseEntity.ok(response);

        } else {
            log.info("Token is not verified");
            throw new BadApiRequestException("Invalid token");
        }
    }

    @Override
    public ResponseEntity<JwtResponse> regenerateToken(RefreshTokenRequest request) {

        RefreshTokenDto refreshTokenDto = refreshTokenService.findByToken(request.getRefreshToken());

        RefreshTokenDto refreshTokenDto1 = refreshTokenService.verifyRefreshToken(refreshTokenDto);

        UserDto user = refreshTokenService.getUser(refreshTokenDto1);

        String jwtToken = jwtHelper.generateToken(modelMapper.map(user, User.class));

        JwtResponse response = JwtResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshTokenDto)
                .user(user)
                .build();
        return ResponseEntity.ok(response);

    }


}
