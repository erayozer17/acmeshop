package com.erayoezer.acmeshop.service;

import com.erayoezer.acmeshop.model.User;
import com.erayoezer.acmeshop.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Value("${base.url}")
    private String baseUrl;

    public User saveUser(String username, String email, String password, String timeZone, String gmtOffSet, String confirmationKey) {
        // TODO: add validation
        User user = new User();
        user.setFullName(username);
        user.setEmail(email);
        if (timeZone.isEmpty()) {
            gmtOffSet = "00:00";
        }
        user.setTimeZone(timeZone);
        user.setGmtOffSet(gmtOffSet);
        user.setConfirmationKey(confirmationKey);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void sendConfirmationEmail(String email, String confirmationKey) {
        String confirmationUrl = baseUrl + "confirm/" + confirmationKey;
        mailService.sendEmail(
                email,
                "Confirm your account",
                "Go to the following link to confirm your account: " + confirmationUrl);
    }

    public void confirmUserByConfirmationKey(String confirmationKey) {
        Optional<User> user = userRepository.findByConfirmationKey(confirmationKey);
        if (user.isEmpty()) {
            logger.error("User cannot be found. Confirmation key: {}", confirmationKey);
            return;
        }
        User retUser = user.get();
        retUser.setConfirmed(true);
        userRepository.save(retUser);
    }
}
