package com.unibox.controller;

import com.unibox.model.Complaint;
import com.unibox.model.User;
import com.unibox.service.ComplaintService;
import com.unibox.service.JwtService;
import com.unibox.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private JwtService jwtService;

    // Signup endpoint
    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody User user) {
        Optional<User> existingUser = userService.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().build(); // Email already exists
        }
        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

    // Login endpoint (Phase 1 Step 2)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        Optional<User> userOpt = userService.findByEmail(loginRequest.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
        User user = userOpt.get();

        // Simple password check (replace with hashing in production)
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        // Generate JWT token with role "USER"
        String token = jwtService.generateToken(user.getEmail(), "USER", null);

        return ResponseEntity.ok(token);
    }

    // Get complaints for a user
    @GetMapping("/{userId}/complaints")
    public ResponseEntity<List<Complaint>> getUserComplaints(@PathVariable Integer userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Complaint> complaints = complaintService.findByUser(user.get());
        return ResponseEntity.ok(complaints);
    }
}
