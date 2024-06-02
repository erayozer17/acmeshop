package com.erayoezer.acmeshop.controller;

import com.erayoezer.acmeshop.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

// TODO: refactor logic into services
@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("isAuthenticated", false);
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username, @RequestParam String password, Model model) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/home";
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("isAuthenticated", false);
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@RequestParam String timezoneInput, @RequestParam String username, @RequestParam String email, @RequestParam String password) {
        ZoneId zoneId;
        try {
            zoneId = ZoneId.of(timezoneInput);
        } catch (DateTimeException dateTimeException) {
            logger.error("Invalid ID for ZoneOffset, invalid format: {}", timezoneInput);
            return "redirect:/signup";
        }
        String gmtOffset = zoneId.getRules().getOffset(Instant.now()).toString();
        String confirmationKey = UUID.randomUUID().toString();
        userService.saveUser(username, email, password, timezoneInput, gmtOffset, confirmationKey);
        userService.sendConfirmationEmail(email, confirmationKey);
        return "redirect:/confirmation";
    }

    @GetMapping("/confirmation")
    public String showConfirmation(Model model) {
        model.addAttribute("isAuthenticated", false);
        return "confirmation";
    }

    @GetMapping("/confirm/{uuid}")
    public String confirmUser(@PathVariable String uuid) {
        userService.confirmUserByConfirmationKey(uuid);
        return "redirect:/login";
    }
}
