package com.taskmanager.userservice.controller;

import com.taskmanager.userservice.model.User;
import com.taskmanager.userservice.repo.UserRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private final UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        try {
            userRepository.count();
            return Map.of("status", "UP", "service", "users");
        } catch (Exception e) {
            return Map.of("status", "DOWN", "error", e.getMessage());
        }
    }

    @GetMapping("/api/users")
    public List<User> list() {
        return userRepository.findAllByOrderByIdAsc();
    }

    @PostMapping("/api/users")
    public ResponseEntity<?> create(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank() || user.getEmail() == null || user.getEmail().isBlank()){
            return ResponseEntity.badRequest().body(Map.of("error", "Name and email are required"));
        }
        try {
            User savedUser = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email already exists"));
        }
    }
}
