package com.ecommerce.service;

import com.ecommerce.repository.UserReository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ecommerce.entitiy.User;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.List;


@Service
@Transactional

public class UserService implements UserDetailsService{
    @Autowired
    private UserReository userReository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userReository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userReository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userReository.findByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userReository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userReository.existsByEmail(email);
    }

    public List<User> getAllUsers() {
        return userReository.findAll();
    }
    public void deleteUser(Long id) {
        userReository.deleteById(id);
    }

    public User createUser(User user) {
        if(userReository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username or Email already exists");
        }
        if(userReository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userReository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser=UserReository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

                if(existingUser.getUsername() != updatedUser.getUsername() && userReository.existsByUsername(updatedUser.getUsername())) {
                    throw new RuntimeException("Username already exists");
                }

                if(existingUser.getEmail() != updatedUser.getEmail() && userReository.existsByEmail(updatedUser.getEmail())) {
                    throw new RuntimeException("Email already exists");
                }

                existingUser.setUsername(updatedUser.getUsername());
                existingUser.setEmail(updatedUser.getEmail());
                if(!updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }
                return userReository.save(existingUser);

        
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userReository.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + identifier));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),List.of());
        
    }

    
}
