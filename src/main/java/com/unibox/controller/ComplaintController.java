package com.unibox.controller;

import com.unibox.dto.ComplaintUpdateRequest;
import com.unibox.model.Complaint;
import com.unibox.model.Department;
import com.unibox.model.User;
import com.unibox.repository.ComplaintRepository;
import com.unibox.repository.DepartmentRepository;
import com.unibox.repository.UserRepository;
import com.unibox.service.ComplaintService;
import com.unibox.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ComplaintService complaintService;
    private final JwtService jwtService;
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";


    @GetMapping
    public List<Complaint> getAllComplaints() {
        List<Complaint> complaints = complaintRepository.findAll();
        System.out.println("[DEBUG] Complaints fetched: " + complaints.size());
        complaints.forEach(c -> System.out.println("Complaint ID: " + c.getId()));
        return complaints;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComplaint(@PathVariable Integer id, HttpServletRequest request) {
        String role = jwtService.extractRole(request);
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        Optional<Complaint> complaintOpt = complaintRepository.findById(id);
        if (complaintOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Complaint not found");
        }

        complaintRepository.deleteById(id);
        return ResponseEntity.ok("Complaint deleted successfully");
    }


    @GetMapping("/user")
    public ResponseEntity<?> getMyComplaints(HttpServletRequest request) {
        String email = jwtService.extractEmail(request);
        String role = jwtService.extractRole(request);
        System.out.println("[DEBUG] User complaints request. Email: " + email + ", Role: " + role);

        if (!"USER".equals(role)) {
            System.out.println("[WARN] Access denied for role: " + role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            System.out.println("[ERROR] User not found: " + email);
            return ResponseEntity.badRequest().body("User not found");
        }

        List<Complaint> complaints = complaintRepository.findByUser(userOpt.get());
        System.out.println("[DEBUG] Complaints found for user: " + complaints.size());
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<?> getComplaintsByDepartment(@PathVariable Integer departmentId) {
        System.out.println("[DEBUG] Department complaints requested for departmentId: " + departmentId);
        Optional<Department> deptOpt = departmentRepository.findById(departmentId);
        if (deptOpt.isEmpty()) {
            System.out.println("[ERROR] Department not found: " + departmentId);
            return ResponseEntity.badRequest().body("Department not found");
        }
        List<Complaint> complaints = complaintRepository.findByDepartment(deptOpt.get());
        System.out.println("[DEBUG] Complaints found for department: " + complaints.size());
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/department/me")
    public ResponseEntity<?> getComplaintsForLoggedInDepartment(HttpServletRequest request) {
        Long departmentId = jwtService.extractDepartmentIdFromRequest(request);

        if (departmentId == null) {
            System.out.println("[ERROR] Invalid token or department ID missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or department ID missing");
        }

        Optional<Department> deptOpt = departmentRepository.findById(departmentId.intValue());
        if (deptOpt.isEmpty()) {
            System.out.println("[ERROR] Department not found for ID from token: " + departmentId);
            return ResponseEntity.badRequest().body("Department not found");
        }

        List<Complaint> complaints = complaintRepository.findByDepartment(deptOpt.get());
        System.out.println("[DEBUG] Complaints found for logged-in department: " + complaints.size());
        return ResponseEntity.ok(complaints);
    }

    // ✅ UPDATED - department updates complaint status and optionally reply
    @PutMapping("/{complaintId}/update")
    public ResponseEntity<?> updateComplaint(
            @PathVariable Integer complaintId,
            @RequestBody @Valid ComplaintUpdateRequest updateRequest,
            HttpServletRequest request) {

        Long deptIdFromToken = jwtService.extractDepartmentIdFromRequest(request);
        System.out.println("[DEBUG] Department updating complaint. ComplaintId: " + complaintId + ", DeptId from token: " + deptIdFromToken);

        if (deptIdFromToken == null) {
            System.out.println("[ERROR] Invalid token or no department ID found.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        Optional<Complaint> optionalComplaint = complaintRepository.findById(complaintId);
        if (optionalComplaint.isEmpty()) {
            System.out.println("[ERROR] Complaint not found: " + complaintId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Complaint not found");
        }

        Complaint complaint = optionalComplaint.get();

        if (!complaint.getDepartment().getId().equals(deptIdFromToken.intValue())) {
            System.out.println("[WARN] Access denied. Complaint dept: " + complaint.getDepartment().getId() + ", token dept: " + deptIdFromToken);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        // ✅ always update status
        complaint.setStatus(Complaint.Status.valueOf(updateRequest.getStatus().toUpperCase()));

        // ✅ only update reply if present and not blank
        if (updateRequest.getReply() != null && !updateRequest.getReply().trim().isEmpty()) {
            complaint.setReply(updateRequest.getReply());
        }

        complaintRepository.save(complaint);
        System.out.println("[INFO] Complaint updated successfully: " + complaintId);

        // ✅ update message
        return ResponseEntity.ok("Complaint status updated successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getComplaintById(@PathVariable Integer id, HttpServletRequest request) {
        String role = jwtService.extractRole(request);
        String email = jwtService.extractEmail(request);

        System.out.println("[DEBUG] Fetching complaint by ID: " + id + ", Role: " + role + ", Email: " + email);

        Complaint complaint = complaintService.getById(id);
        if (complaint == null) {
            System.out.println("[ERROR] Complaint not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Complaint not found");
        }

        switch (role) {
            case "USER":
                Optional<User> userOpt = userRepository.findByEmail(email);
                if (userOpt.isEmpty() || !complaint.getUser().getId().equals(userOpt.get().getId())) {
                    System.out.println("[WARN] Access denied for USER: " + email);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                break;

            case "DEPARTMENT":
                Long deptIdFromToken = jwtService.extractDepartmentIdFromRequest(request);
                if (deptIdFromToken == null || !complaint.getDepartment().getId().equals(deptIdFromToken.intValue())) {
                    System.out.println("[WARN] Access denied for DEPARTMENT: TokenDeptId=" + deptIdFromToken + ", ComplaintDeptId=" + complaint.getDepartment().getId());
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                break;

            case "ADMIN":
                // Admin access allowed without checks
                break;

            default:
                System.out.println("[ERROR] Invalid role: " + role);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid role");
        }

        System.out.println("[INFO] Complaint fetched successfully for ID: " + id);
        return ResponseEntity.ok(complaint);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createComplaint(
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam("departmentId") Integer departmentId,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request) {

        String email = jwtService.extractEmail(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        Optional<Department> deptOpt = departmentRepository.findById(departmentId);
        if (deptOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid department");
        }

        Complaint complaint = new Complaint();
        complaint.setDescription(description);
        complaint.setLocation(location);
        complaint.setLatitude(latitude);
        complaint.setLongitude(longitude);
        complaint.setDepartment(deptOpt.get());
        complaint.setUser(userOpt.get());
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setStatus(Complaint.Status.PENDING);

        if (image != null && !image.isEmpty()) {
            try {
                String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                File dest = new File(uploadDir, filename);
                image.transferTo(dest);
                complaint.setImageUrl("/uploads/" + filename);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed");
            }
        }

        complaintRepository.save(complaint);
        return ResponseEntity.status(HttpStatus.CREATED).body("Complaint created successfully");
    }

}
