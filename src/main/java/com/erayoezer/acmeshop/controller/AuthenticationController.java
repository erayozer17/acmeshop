package com.erayoezer.acmeshop.controller;

import com.erayoezer.acmeshop.dto.LoginUserDto;
import com.erayoezer.acmeshop.dto.RegisterUserDto;
import com.erayoezer.acmeshop.model.LoginResponse;
import com.erayoezer.acmeshop.model.User;
import com.erayoezer.acmeshop.service.AuthenticationService;
import com.erayoezer.acmeshop.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        Optional<User> authenticatedUser = authenticationService.authenticate(loginUserDto);

        if (authenticatedUser.isEmpty()) {
            return ResponseEntity.status(403).build();
        }

        String jwtToken = jwtService.generateToken(authenticatedUser.get());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}
