package com.erayoezer.acmeshop.authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.erayoezer.acmeshop.model.dto.LoginUserDto;
import com.erayoezer.acmeshop.model.dto.RegisterUserDto;
import com.erayoezer.acmeshop.model.User;
import com.erayoezer.acmeshop.repository.UserRepository;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

//    @InjectMocks
//    private AuthenticationService authenticationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    public void testSignup() {
//        RegisterUserDto input = new RegisterUserDto();
//        input.setUserName("John Doe");
//        input.setEmail("john.doe@example.com");
//        input.setPassword("password");
//
//        User user = new User();
//        user.setFullName(input.getUserName());
//        user.setEmail(input.getEmail());
//        user.setPassword(passwordEncoder.encode(input.getPassword()));
//
//        when(userRepository.save(any(User.class))).thenReturn(user);
//
//        User savedUser = authenticationService.signup(input);
//
//        assertNotNull(savedUser);
//        assertEquals(input.getUserName(), savedUser.getFullName());
//        assertEquals(input.getEmail(), savedUser.getEmail());
//        assertTrue(passwordEncoder.matches(input.getPassword(), savedUser.getPassword()));
    }

    @Test
    public void testAuthenticateSuccess() {
//        LoginUserDto input = new LoginUserDto();
//        input.setEmail("john.doe@example.com");
//        input.setPassword("password");
//
//        User user = new User();
//        user.setEmail(input.getEmail());
//        user.setPassword(passwordEncoder.encode(input.getPassword()));
//
//        Authentication authentication = mock(Authentication.class);
//
//        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
//        when(authentication.isAuthenticated()).thenReturn(true);
//        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.of(user));
//
//        Optional<User> authenticatedUser = authenticationService.authenticate(input);
//
//        assertTrue(authenticatedUser.isPresent());
//        assertEquals(input.getEmail(), authenticatedUser.get().getEmail());
    }

    @Test
    public void testAuthenticateFailure() {
//        LoginUserDto input = new LoginUserDto();
//        input.setEmail("john.doe@example.com");
//        input.setPassword("wrongpassword");
//        Authentication authentication = mock(Authentication.class);
//        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(new BadCredentialsException("Invalid credentials"));
//
//        Optional<User> authenticatedUser = authenticationService.authenticate(input);
//
//        assertFalse(authenticatedUser.isPresent());
    }
}

