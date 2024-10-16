package com.example.timetracker2.repository;

import com.example.timetracker2.model.ProjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Integer> {
    List<ProjectAssignment> findByEmployeeIdAndProjectId(Integer employeeId, Integer projectId);
}
