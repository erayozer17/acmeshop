package com.erayoezer.acmeshop.controller;

import com.erayoezer.acmeshop.model.dto.LoginUserDto;
import com.erayoezer.acmeshop.model.dto.RegisterUserDto;
import com.erayoezer.acmeshop.model.LoginResponse;
import com.erayoezer.acmeshop.model.User;
import com.erayoezer.acmeshop.model.validator.LoginUserDtoValidator;
import com.erayoezer.acmeshop.model.validator.RegisterUserDtoValidator;
import com.erayoezer.acmeshop.service.AuthenticationService;
import com.erayoezer.acmeshop.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @InitBinder("RegisterUserDto")
    protected void initRegisterBinder(WebDataBinder binder) {
        binder.addValidators(new RegisterUserDtoValidator());
    }

    @InitBinder("LoginUserDto")
    protected void initLoginBinder(WebDataBinder binder) {
        binder.addValidators(new LoginUserDtoValidator());
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        try {
            User registeredUser = authenticationService.signup(registerUserDto);
            logger.info("User registered successfully: {}", registeredUser.getEmail());
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            logger.error("Error occurred during registration: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        try {
            Optional<User> authenticatedUser = authenticationService.authenticate(loginUserDto);
            if (authenticatedUser.isEmpty()) {
                logger.warn("Authentication failed for user: {}", loginUserDto.getEmail());
                return ResponseEntity.status(403).build();
            }
            String jwtToken = jwtService.generateToken(authenticatedUser.get());
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(jwtToken);
            loginResponse.setExpiresIn(jwtService.getExpirationTime());

            logger.info("User logged in successfully: {}", loginUserDto.getEmail());
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            logger.error("Error occurred during authentication: ", e);
            return ResponseEntity.status(500).build();
        }
    }
}
