package com.unibox.service;

import com.unibox.model.Complaint;
import com.unibox.model.Department;
import com.unibox.model.User;
import com.unibox.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    public Complaint saveComplaint(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    public Optional<Complaint> findById(Integer id) {
        return complaintRepository.findById(id);
    }

    public Complaint getById(Integer id) {
        return complaintRepository.findById(id).orElse(null);
    }

    public List<Complaint> findByUser(User user) {
        return complaintRepository.findByUser(user);
    }

    // No change needed here; used by your new endpoint in controller
    public List<Complaint> findByDepartment(Department department) {
        return complaintRepository.findByDepartment(department);
    }
}
