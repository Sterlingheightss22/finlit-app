package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Called after Firebase login to register user in your DB
    @PostMapping("/register")
    public User registerUser(@RequestBody User user,
                             Authentication authentication) {
        // Use Firebase UID as a check to avoid duplicates
        String firebaseUid = authentication != null ? (String) authentication.getPrincipal() : "mock-uid-" + user.getUsername();

        User existing = userRepository.findByUsername(user.getUsername());
        if (existing != null) {
            return existing;
        }

        user.setEmail(user.getEmail());
        user.setUsername(user.getUsername());
        return userRepository.save(user);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        User existing = userRepository.findByUsername(user.getUsername());
        if (existing != null) {
            return existing;
        }
        return userRepository.save(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}