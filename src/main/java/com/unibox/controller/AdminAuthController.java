package com.unibox.controller;

import com.unibox.dto.LoginRequest;
import com.unibox.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        System.out.println("Configured admin email: " + adminEmail);
        System.out.println("Configured admin password: " + adminPassword);
        System.out.println("Login request email: " + request.getEmail());
        System.out.println("Login request password: " + request.getPassword());
        if (!request.getEmail().trim().equals(adminEmail) || !request.getPassword().trim().equals(adminPassword)) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtService.generateToken(adminEmail, "ADMIN", null);
        return ResponseEntity.ok(token);
    }

}
