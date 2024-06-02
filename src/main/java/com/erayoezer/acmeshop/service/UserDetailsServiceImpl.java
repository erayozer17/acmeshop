package com.erayoezer.acmeshop.service;

import com.erayoezer.acmeshop.model.User;
import com.erayoezer.acmeshop.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        User retUser = user.get();
        if (!retUser.isConfirmed()) {
            // TODO: implement an appropriate exception
            throw new UsernameNotFoundException("User is not confirmed. Email: " + username);
        }
        return user.get();
    }
}
