package com.unibox.repository;

import com.unibox.model.Complaint;
import com.unibox.model.Department;
import com.unibox.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Integer> {
    List<Complaint> findByUser(User user);
    List<Complaint> findByDepartment(Department department);
}
