package com.blog.cutom_blog.services;


import com.blog.cutom_blog.constants.ERole;
import com.blog.cutom_blog.dtos.SignupRequest;
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

    public User createUser(SignupRequest signUpRequest) {
        User user = User.builder()
            .username(signUpRequest.getUsername())
            .firstName(signUpRequest.getFirstName())
            .lastName(signUpRequest.getLastName())
            .email(signUpRequest.getEmail())
            .password(encoder.encode(signUpRequest.getPassword()))
            .build();

        Role userRole = roleRepository.findByName(signUpRequest.getRole())
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        user.setRoleId(userRole.getId());
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}