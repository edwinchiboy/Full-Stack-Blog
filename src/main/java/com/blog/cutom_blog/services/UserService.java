package com.blog.cutom_blog.services;


import com.blog.cutom_blog.constants.ERole;
import com.blog.cutom_blog.dtos.AdminSignupRequest;
import com.blog.cutom_blog.models.Registration;
import com.blog.cutom_blog.models.Role;
import com.blog.cutom_blog.models.User;
import com.blog.cutom_blog.repositories.RoleRepository;
import com.blog.cutom_blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    public User createUser(Registration registration, String password) {
        // Default behavior: create reader user
        return createReaderUser(registration, password);
    }

    public User createReaderUser(Registration registration, String password) {
        // Generate username from email (part before @) or firstName-lastName, max 50 chars
        String username = generateUsername(registration.getEmail(), registration.getFirstName(), registration.getLastName());

        User user = User.builder()
            .username(username)
            .firstName(registration.getFirstName())
            .lastName(registration.getLastName())
            .email(registration.getEmail())
            .password(encoder.encode(password))
            .build();

        // Assign READER role
        Role readerRole = roleRepository.findByName(ERole.ROLE_READER)
            .orElseThrow(() -> new RuntimeException("Error: Reader role is not found."));

        user.setRoleId(readerRole.getId());
        return userRepository.save(user);
    }

    public User createAdminUserFromRegistration(Registration registration, String password) {
        // Generate username from email (part before @) or firstName-lastName, max 50 chars
        String username = generateUsername(registration.getEmail(), registration.getFirstName(), registration.getLastName());

        User user = User.builder()
            .username(username)
            .firstName(registration.getFirstName())
            .lastName(registration.getLastName())
            .email(registration.getEmail())
            .password(encoder.encode(password))
            .build();

        // Assign ADMIN role
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
            .orElseThrow(() -> new RuntimeException("Error: Admin role is not found."));

        user.setRoleId(adminRole.getId());
        return userRepository.save(user);
    }

    private String generateUsername(String email, String firstName, String lastName) {
        // Try using email username (before @)
        String emailUsername = email.substring(0, email.indexOf('@'));
        if (emailUsername.length() <= 50 && !userRepository.existsByUsername(emailUsername)) {
            return emailUsername;
        }

        // Try firstName-lastName, truncated to 50 chars
        String fullNameUsername = String.format("%s-%s", firstName, lastName);
        if (fullNameUsername.length() <= 50) {
            // Check if it exists, if so add a number
            if (!userRepository.existsByUsername(fullNameUsername)) {
                return fullNameUsername;
            }
        } else {
            // Truncate to 50 chars
            fullNameUsername = fullNameUsername.substring(0, 50);
        }

        // If username exists, append a unique number
        String baseUsername = fullNameUsername;
        int counter = 1;
        while (userRepository.existsByUsername(fullNameUsername)) {
            fullNameUsername = String.format("%s%d", baseUsername.substring(0, Math.min(baseUsername.length(), 47)), counter);
            counter++;
        }

        return fullNameUsername;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User createAdminUser(AdminSignupRequest signupRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Generate username from email
        String username = generateUsername(signupRequest.getEmail(), signupRequest.getFirstName(), signupRequest.getLastName());

        User user = User.builder()
            .username(username)
            .firstName(signupRequest.getFirstName())
            .lastName(signupRequest.getLastName())
            .email(signupRequest.getEmail())
            .password(encoder.encode(signupRequest.getPassword()))
            .build();

        // Assign ROLE_ADMIN role
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
            .orElseThrow(() -> new RuntimeException("Error: Admin role is not found."));

        user.setRoleId(adminRole.getId());
        return userRepository.save(user);
    }
}