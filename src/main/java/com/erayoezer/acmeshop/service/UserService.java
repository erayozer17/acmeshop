package com.erayoezer.acmeshop.service;

import com.erayoezer.acmeshop.model.User;
import com.erayoezer.acmeshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(String username, String email, String password, String timeZone, String gmtOffSet) {
        // TODO: add validation
        User user = new User();
        user.setFullName(username);
        user.setEmail(email);
        if (timeZone.isEmpty()) {
            gmtOffSet = "00:00";
        }
        user.setTimeZone(timeZone);
        user.setGmtOffSet(gmtOffSet);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
