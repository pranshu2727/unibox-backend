package com.unibox.service;

import com.unibox.model.Department;
import com.unibox.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Optional<Department> findByUserEmail(String email) {
        return departmentRepository.findByUserEmail(email);
    }

    public Optional<Department> findById(Integer id) {
        return departmentRepository.findById(id);
    }
}
