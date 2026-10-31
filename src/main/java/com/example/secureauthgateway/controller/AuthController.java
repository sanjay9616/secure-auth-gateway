
package com.example.secureauthgateway.controller;

import com.example.secureauthgateway.dto.LoginRequest;
import com.example.secureauthgateway.dto.LoginResponse;
import com.example.secureauthgateway.dto.SignupRequest;
import com.example.secureauthgateway.dto.UserResponse;
import com.example.secureauthgateway.service.AuthService;
import com.example.secureauthgateway.service.UserService;

import org.springframework.security.core.Authentication;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService,
        UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(
            @Valid @RequestBody SignupRequest request) {

        UserResponse response = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            Authentication authentication) {

        String email = authentication.getName();

        UserResponse userResponse = userService.getUserByEmail(email);

        return ResponseEntity.ok(userResponse);
    }
}
