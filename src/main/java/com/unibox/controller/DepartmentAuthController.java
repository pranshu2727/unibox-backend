package com.unibox.controller;

import com.unibox.dto.DepartmentLoginRequest;
import com.unibox.model.Department;
import com.unibox.repository.DepartmentRepository;
import com.unibox.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class DepartmentAuthController {

    private final DepartmentRepository departmentRepository;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DepartmentLoginRequest request) {
        Optional<Department> optionalDepartment = departmentRepository.findByUserEmail(request.getEmail());

        if (optionalDepartment.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        Department department = optionalDepartment.get();

        if (!department.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // Generate token with department id
        String token = jwtService.generateToken(department.getUserEmail(), "DEPARTMENT", department.getId().longValue());

        return ResponseEntity.ok(Collections.singletonMap("token", token));


    }
}
