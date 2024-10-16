package com.example.timetracker2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "employee")
@Data
public class Employee {
    @Id
    private Integer id;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProjectAssignment> projectAssignments;
}
