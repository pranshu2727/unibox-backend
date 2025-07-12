package com.unibox.controller;

import com.unibox.model.Department;
import com.unibox.repository.DepartmentRepository;
import com.unibox.service.DepartmentService;
import com.unibox.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.unibox.dto.DepartmentDTO;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentRepository departmentRepository;
    private final JwtService jwtService;

    // Get all departments (Admin only)
    @GetMapping
    public ResponseEntity<?> getAllDepartments(HttpServletRequest request) {
        if (!"ADMIN".equals(jwtService.extractRole(request))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        List<Department> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    // Add a new department (Admin only)
    @PostMapping("/add")
    public ResponseEntity<?> addDepartment(@RequestBody Department department, HttpServletRequest request) {
        if (!"ADMIN".equals(jwtService.extractRole(request))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        Department saved = departmentService.saveDepartment(department);
        return ResponseEntity.ok(saved);
    }

    // Delete a department by ID (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Integer id, HttpServletRequest request) {
        if (!"ADMIN".equals(jwtService.extractRole(request))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        if (!departmentRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department not found");
        }

        departmentRepository.deleteById(id);
        return ResponseEntity.ok("Department deleted successfully");
    }

    @RestController
    @RequestMapping("/api/public")
    public class PublicDepartmentController {

        private final DepartmentService departmentService;

        public PublicDepartmentController(DepartmentService departmentService) {
            this.departmentService = departmentService;
        }

        @GetMapping("/departments")
        public ResponseEntity<List<DepartmentDTO>> getAllDepartmentsPublic() {
            List<Department> departments = departmentService.getAllDepartments();

            // Return only id and name (you can use DTO for safety)
            List<DepartmentDTO> dtoList = departments.stream()
                    .map(d -> new DepartmentDTO(d.getId(), d.getName()))
                    .toList();

            return ResponseEntity.ok(dtoList);
        }
    }

}
