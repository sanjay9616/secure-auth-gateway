package com.example.secureauthgateway.service;

import com.example.secureauthgateway.dto.UserRequest;
import com.example.secureauthgateway.dto.UserResponse;
import com.example.secureauthgateway.entity.User;
import com.example.secureauthgateway.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return mapToResponse(user);
    }

    public UserResponse updateUser(Long id, UserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }

    private UserResponse mapToResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setEnabled(user.isEnabled());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }

    public UserResponse getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return mapToResponse(user);
    }
}