package com.blog.cutom_blog.config.security_configuration;


import com.blog.cutom_blog.models.User;
import com.blog.cutom_blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    com.blog.cutom_blog.services.RoleService roleService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find by username first, then by email
        User user = userRepository.findByUsername(username)
            .or(() -> userRepository.findByEmail(username))
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username or email: " + username));

        // Create UserDetailsImpl instance with injected roleService
        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(null, null, null, null, null);
        userDetailsImpl.setRoleService(roleService);
        return userDetailsImpl.build(user);
    }
}