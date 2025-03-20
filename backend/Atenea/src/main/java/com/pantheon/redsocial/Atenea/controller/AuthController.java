package com.pantheon.redsocial.Atenea.controller;

import com.pantheon.redsocial.Atenea.security.JwtTokenProvider;
import com.pantheon.redsocial.Atenea.payload.LoginRequest;
import com.pantheon.redsocial.Atenea.payload.JwtAuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            String jwt = tokenProvider.generateToken(authentication);
            logger.info("Successful login for user: {}", loginRequest.getUsername());
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));

        } catch (BadCredentialsException ex) {
            logger.warn("Failed login attempt for user: {}", loginRequest.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
